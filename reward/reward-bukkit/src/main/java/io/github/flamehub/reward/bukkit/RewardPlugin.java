package io.github.flamehub.reward.bukkit;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.command.argument.LocationArgument;
import io.github.flamehub.commons.bukkit.command.argument.WorldArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.reward.api.RewardReceivedEntry;
import io.github.flamehub.reward.api.RewardReceivedEntryRepository;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class RewardPlugin extends BukkitModule {

  private RewardConfig rewardConfig;
  private RewardReceivedEntryRepository rewardReceivedEntryRepository;

  @Override
  public void onEnable() {
    super.onEnable();

    this.rewardConfig = this.flameConfigService.getOrCreate(this.getDataFolder(),
        RewardConfig.class);

    this.rewardReceivedEntryRepository = new RewardReceivedEntryRepository(
        DatastoreFactory.create(this.databaseConnector.getMongoClient(), "global",
            RewardReceivedEntry.class),
        RewardReceivedEntry.class
    );
    this.redisMessenger.subscribe(this.networkServerCache.getCurrent().getName(),
        new RewardHandler(this.flameDispatcher, rewardConfig));

    LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("flamehub-reward")
            .nativePermissions(false)
        )
        .argument(Location.class, new LocationArgument())
        .argument(World.class, new WorldArgument())

        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(this.messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(this.messagesService))

        .commands(LiteCommandsAnnotations.of(
            new RewardCommand(this.rewardReceivedEntryRepository, networkServerCache)
        ))

        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();

  }
}

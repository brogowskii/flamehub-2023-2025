package io.github.flamehub.code;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.bukkit.user.UserDatabaseListener;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.reward.bukkit.RewardPlugin;
import io.github.flamehub.timeplayed.user.TimePlayedUserCache;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public final class CodePlugin extends BukkitModule {

  private TimePlayedUserCache timePlayedUserCache;

  private CodeConfig codeConfig;
  private CodeUserCache codeUserCache;
  private CodeUserRepository codeUserRepository;
  private CodeUserFactory codeUserFactory;

  @Override
  public void onEnable() {
    super.onEnable();

    timePlayedUserCache = getService(TimePlayedUserCache.class);
    codeConfig = flameConfigService.getOrCreate(CodeConfig.class);
    codeUserRepository = new CodeUserRepository(
        DatastoreFactory.create(databaseConnector.getMongoClient(),
            networkServerFacade.getCurrent().getCategory(), CodeUser.class));
    codeUserCache = new CodeUserCache(codeUserRepository);
    codeUserFactory = new CodeUserFactory();

    final PluginManager pluginManager = getServer().getPluginManager();
    pluginManager.registerEvents(
        new UserDatabaseListener<>(flameDispatcher, pluginManager, codeUserCache,
            codeUserRepository, codeUserFactory), this);

    LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("codes")
            .nativePermissions(false)
        )
        .argument(Player.class, new PlayerArgument(messagesService))
        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(messagesService))

        .commands(LiteCommandsAnnotations.of(
            new CodeCommand(timePlayedUserCache, flameDispatcher, flameConfigService,
                codeConfig, codeUserCache, codeUserRepository, RewardPlugin.INSTANCE.getRewardReceivedEntryRepository())
        ))

        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();

  }
}

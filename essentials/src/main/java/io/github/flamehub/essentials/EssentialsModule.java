package io.github.flamehub.essentials;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.argument.ArgumentKey;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.LiteBukkitSettings;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.command.argument.LocationArgument;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.bukkit.network.player.NetworkPlayerArgument;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.essentials.banitem.BanItemConfigurator;
import io.github.flamehub.essentials.banitem.BanItemFacade;
import io.github.flamehub.essentials.command.CommandConfigurator;
import io.github.flamehub.essentials.command.argument.GameModeArgument;
import io.github.flamehub.essentials.privatemessage.PrivateMessageConfigurator;
import io.github.flamehub.essentials.spawn.SpawnConfigurator;
import io.github.flamehub.essentials.spawn.SpawnFacade;
import io.github.flamehub.essentials.teleport.TeleportConfigurator;
import io.github.flamehub.essentials.teleport.TeleportFacade;
import io.github.flamehub.essentials.user.EssentialsUserConfigurator;
import io.github.flamehub.essentials.user.EssentialsUserFacade;
import io.github.flamehub.essentials.vanish.VanishConfigurator;
import io.github.flamehub.essentials.vanish.VanishFacade;
import io.github.flamehub.essentials.warp.WarpConfigurator;
import io.github.flamehub.essentials.warp.WarpFacade;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

public final class EssentialsModule extends BukkitModule {

  private LiteCommands<CommandSender> liteCommands;

  private EssentialsUserFacade essentialsUserFacade;
  private BanItemFacade banItemFacade;
  private WarpFacade warpFacade;
  private VanishFacade vanishFacade;
  private SpawnFacade spawnFacade;
  private TeleportFacade teleportFacade;

  @Override
  public void onEnable() {
    super.onEnable();

    final LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> builder = LiteBukkitFactory.builder(
            "essentials", this)
        .argument(Player.class, new PlayerArgument(super.messagesService))
        .argument(NetworkPlayer.class,
            new NetworkPlayerArgument(super.messagesService, super.networkPlayerCache,
                super.networkServerCache))
        .argument(GameMode.class, new GameModeArgument(super.messagesService))
        .argument(Location.class, new LocationArgument())
        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(super.messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(super.messagesService))
        .argumentSuggester(String.class, ArgumentKey.of("playerName"),
            (invocation, argument, context) -> Bukkit.getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .collect(SuggestionResult.collector())
        )
        .schematicGenerator(SchematicFormat.angleBrackets());

    final ServicesManager servicesManager = this.getServer().getServicesManager();

    final WarpConfigurator warpConfigurator = new WarpConfigurator();
    this.warpFacade = warpConfigurator.warpFacade(this, builder, this.flameConfigService,
        this.teleporterService);

    final VanishConfigurator vanishConfigurator = new VanishConfigurator();
    this.vanishFacade = vanishConfigurator.vanishFacade(
        builder,
        this.databaseConnector.getMongoClient(),
        this,
        this.networkServerCache.getCurrent().getCategory(),
        this.flameDispatcher,
        this.messagesService
    );

    final EssentialsUserConfigurator essentialsUserConfigurator = new EssentialsUserConfigurator();
    this.essentialsUserFacade = essentialsUserConfigurator.essentialsUserFacade(
        this,
        this.flameDispatcher,
        this.databaseConnector.getMongoClient(),
        this.networkServerCache.getCurrent().getCategory()
    );

    new CommandConfigurator(builder, this.messagesService);
    new PrivateMessageConfigurator(
        builder,
        this.flameDispatcher,
        this.redisMessenger,
        this.messagesService,
        this.networkPlayerCache,
        this.essentialsUserFacade,
        this.networkServerCache.getCurrent().getName()
    );

    final SpawnConfigurator spawnConfigurator = new SpawnConfigurator();
    this.spawnFacade = spawnConfigurator.spawnFacade(this, builder, this.flameConfigService,
        this.teleporterService);
    servicesManager.register(SpawnFacade.class, this.spawnFacade, this, ServicePriority.Normal);

    final BanItemConfigurator banItemConfigurator = new BanItemConfigurator();
    this.banItemFacade = banItemConfigurator.banItemFacade(this, builder, this.flameConfigService);

    final TeleportConfigurator teleportConfigurator = new TeleportConfigurator();
    this.teleportFacade = teleportConfigurator.teleportFacade(
        this,
        builder,
        this.flameDispatcher,
        this.redisMessenger,
        this.networkServerCache
    );

    this.liteCommands = builder.build();
  }

  @Override
  public void onDisable() {
    this.liteCommands.unregister();
  }
}
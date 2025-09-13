package io.github.flamehub.player.sync;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.player.sync.command.EnderChestPreviewCommand;
import io.github.flamehub.player.sync.command.OfflineInvseeCommand;
import io.github.flamehub.player.sync.command.ResetPlayerCommand;
import io.github.flamehub.player.sync.command.ScanUsersCommand;
import io.github.flamehub.player.sync.command.StopCommand;
import io.github.flamehub.player.sync.data.PlayerDataSyncSaveTask;
import io.github.flamehub.player.sync.data.PlayerSyncData;
import io.github.flamehub.player.sync.data.PlayerSyncDataFactory;
import io.github.flamehub.player.sync.data.PlayerSyncDataListener;
import io.github.flamehub.player.sync.data.PlayerSyncDataRepository;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.scheduler.BukkitScheduler;

public final class PlayerSyncPlugin extends BukkitModule {

  private NetworkMessageService networkMessageService;
  private PlayerSyncDataRepository playerSyncDataRepository;

  private PlayerSyncConfig playerSyncConfig;

  @Override
  public void onEnable() {
    super.onEnable();

    networkMessageService = new NetworkMessageService(redisMessenger, "network_messages");

    final Server server = getServer();
    final ServicesManager servicesManager = server.getServicesManager();
//    this.playerSyncDataFacade = PlayerSyncDataFacadeCreator.create(
//        this,
//        flameDispatcher,
//        redisService,
//        redisMessenger,
//        networkServerCache,
//        ,
//        servicesManager,
//        getServer(),
//        networkMessageService
//    );

    playerSyncConfig = flameConfigService.getOrCreate(PlayerSyncConfig.class);
    playerSyncDataRepository = new PlayerSyncDataRepository(
        DatastoreFactory.create(databaseConnector.getMongoClient(),
            networkServerFacade.getCurrent().getCategory(), PlayerSyncData.class));

    servicesManager.register(PlayerSyncDataRepository.class, playerSyncDataRepository, this, ServicePriority.Normal);

    final PluginManager pluginManager = server.getPluginManager();
    pluginManager.registerEvents(
        new PlayerSyncDataListener(flameDispatcher, playerSyncConfig, playerSyncDataRepository), this);

    final BukkitScheduler scheduler = server.getScheduler();
    scheduler.runTaskTimerAsynchronously(this,
            new PlayerDataSyncSaveTask(playerSyncDataRepository, networkMessageService), 0L,
            20 * 120L
    );

    setupCommands();
  }

  void setupCommands() {
    LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("player-sync")
            .nativePermissions(false)
        )
        .argument(Player.class, new PlayerArgument(messagesService))

        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(messagesService))

        .commands(LiteCommandsAnnotations.of(
            new ScanUsersCommand(flameDispatcher, playerSyncDataRepository),
            new OfflineInvseeCommand(playerSyncDataRepository, networkPlayerCache,
                networkServerFacade),
            new EnderChestPreviewCommand(playerSyncDataRepository, networkPlayerCache,
                networkServerFacade),
            new StopCommand(playerSyncDataRepository),
            new ResetPlayerCommand(playerSyncDataRepository),
            new PlayerSyncCommand(flameConfigService, playerSyncConfig)
        ))

        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();
  }

  @Override
  public void onDisable() {

    for (final Player player : Bukkit.getOnlinePlayers()) {
      player.closeInventory();
    }

    final List<PlayerSyncData> collect = Bukkit.getOnlinePlayers().stream()
        .map(PlayerSyncDataFactory::create)
        .toList();
    playerSyncDataRepository.saveMany(collect);
    getLogger().info("Pomyślnie zapisano dane wszystkich graczy!");
  }

}
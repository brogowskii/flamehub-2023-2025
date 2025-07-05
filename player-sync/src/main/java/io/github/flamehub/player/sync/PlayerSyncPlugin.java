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
import io.github.flamehub.player.sync.command.ScanUsersCommand;
import io.github.flamehub.player.sync.command.StopCommand;
import io.github.flamehub.player.sync.data.PlayerDataSyncSaveTask;
import io.github.flamehub.player.sync.data.PlayerSyncData;
import io.github.flamehub.player.sync.data.PlayerSyncDataFactory;
import io.github.flamehub.player.sync.data.PlayerSyncDataListener;
import io.github.flamehub.player.sync.data.PlayerSyncDataRepository;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

public final class PlayerSyncPlugin extends BukkitModule {

  private NetworkMessageService networkMessageService;
  private PlayerSyncDataRepository playerSyncDataRepository;

  private boolean disabling;

  @Override
  public void onEnable() {
    super.onEnable();
    disabling = false;

    networkMessageService = new NetworkMessageService(redisMessenger, "network_messages");

    final ServicesManager servicesManager = getServer().getServicesManager();
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

    playerSyncDataRepository = new PlayerSyncDataRepository(
        DatastoreFactory.create(databaseConnector.getMongoClient(),
            networkServerCache.getCurrent().getCategory(), PlayerSyncData.class));

    servicesManager.register(PlayerSyncDataRepository.class, playerSyncDataRepository, this,
        ServicePriority.Normal);

    getServer().getPluginManager().registerEvents(
        new PlayerSyncDataListener(redisMessenger, flameDispatcher,
            networkServerCache.getCurrent(), playerSyncDataRepository), this);

    getServer()
        .getScheduler()
        .runTaskTimerAsynchronously(
            this,
            new PlayerDataSyncSaveTask(playerSyncDataRepository, networkServerCache,
                networkMessageService),
            0L, 20 * 120L
        );

    setupCommands();
  }

  void setupCommands() {
    LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("flamehub-sync")
            .nativePermissions(false)
        )
        .argument(Player.class, new PlayerArgument(messagesService))

        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(messagesService))

        .commands(LiteCommandsAnnotations.of(
            new ScanUsersCommand(flameDispatcher, playerSyncDataRepository),
            new OfflineInvseeCommand(playerSyncDataRepository, networkPlayerCache,
                networkServerCache),
            new EnderChestPreviewCommand(playerSyncDataRepository, networkPlayerCache,
                networkServerCache),
            new StopCommand(playerSyncDataRepository)
        ))

        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();
  }

  @Override
  public void onDisable() {
    disabling = true;
    for (final Player player : Bukkit.getOnlinePlayers()) {
      player.closeInventory();
    }

    final List<PlayerSyncData> collect = Bukkit.getOnlinePlayers().stream()
        .map(PlayerSyncDataFactory::create)
        .toList();
    playerSyncDataRepository.saveMany(collect);
    getLogger().info("Pomyślnie zapisano dane wszystkich graczy!");
  }

  public boolean isDisabling() {
    return disabling;
  }
}
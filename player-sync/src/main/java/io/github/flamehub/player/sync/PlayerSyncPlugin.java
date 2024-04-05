package io.github.flamehub.player.sync;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import io.github.flamehub.commons.bukkit.BukkitPlugin;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.config.MongoConfigService;
import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.player.sync.command.EnderChestPreviewCommand;
import io.github.flamehub.player.sync.command.InventoryCloseListener;
import io.github.flamehub.player.sync.command.OfflineInvseeCommand;
import io.github.flamehub.player.sync.command.ScanUsersCommand;
import io.github.flamehub.player.sync.data.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

import java.util.List;

public final class PlayerSyncPlugin extends BukkitPlugin {

    private NetworkServerCache networkServerCache;
    private RedisMessenger redisMessenger;
    private DatabaseConnector databaseConnector;

    private NetworkMessageService networkMessageService;
    private NetworkPlayerCache networkPlayerCache;

    private BukkitMessagesService messagesService;

    private PlayerSyncConfig playerSyncConfig;
    private PlayerSyncDataRepository playerSyncDataRepository;

    private boolean disabling;

    @Override
    public void onEnable() {
        this.disabling = false;

        MongoConfigService mongoConfigService = getService(MongoConfigService.class);
        this.redisMessenger = getService(RedisMessenger.class);
        this.databaseConnector = getService(DatabaseConnector.class);
        this.messagesService = getService(BukkitMessagesService.class);
        this.networkServerCache = getService(NetworkServerCache.class);
        this.networkPlayerCache = getService(NetworkPlayerCache.class);
        this.networkMessageService = new NetworkMessageService(this.redisMessenger, "network_messages");

        this.playerSyncConfig = mongoConfigService.findOrCreate(PlayerSyncConfig.class, "player_sync", PlayerSyncConfig::new);
        this.playerSyncDataRepository = new PlayerSyncDataRepository(
                DatastoreFactory.create(
                        this.databaseConnector.getMongoClient(),
                        this.networkServerCache.getCurrent().getCategory(),
                        PlayerSyncData.class
                )
        );

        this.getServer()
                .getScheduler()
                .runTaskTimerAsynchronously(
                        this,
                        new PlayerDataSyncSaveTask(this.playerSyncDataRepository, this.networkServerCache, this.networkMessageService),
                        0L, 20 * 120L
                );

        ServicesManager servicesManager = this.getServer().getServicesManager();
        servicesManager.register(PlayerSyncDataRepository.class, this.playerSyncDataRepository, this, ServicePriority.Normal);

        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerSyncDataListener(this, playerSyncConfig, this.flameDispatcher, this.playerSyncDataRepository), this);
        pluginManager.registerEvents(new InventoryCloseListener(this.playerSyncDataRepository), this);
        setupCommands();
    }

    void setupCommands() {
        LiteBukkitFactory.builder()
                .settings(settings -> settings
                        .fallbackPrefix("flamehub-sync")
                        .nativePermissions(false)
                )
                .argument(Player.class, new PlayerArgument(this.messagesService))

                .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

                .missingPermission(new MissingPermissionHandlerImpl(this.messagesService))
                .invalidUsage(new InvalidUsageHandlerImpl(this.messagesService))

                .commands(LiteCommandsAnnotations.of(
                        new ScanUsersCommand(flameDispatcher, this.playerSyncDataRepository),
                        new OfflineInvseeCommand(this.playerSyncDataRepository, this.networkPlayerCache, this.networkServerCache),
                        new EnderChestPreviewCommand(this.playerSyncDataRepository, this.networkPlayerCache, this.networkServerCache)
                ))

                .schematicGenerator(SchematicFormat.angleBrackets())
                .build();
    }

    @Override
    public void onDisable() {
        this.disabling = true;
        List<PlayerSyncData> collect = Bukkit.getOnlinePlayers().stream()
                .map(PlayerSyncDataFactory::create)
                .toList();
        this.playerSyncDataRepository.saveMany(collect);
        this.getLogger().info("Pomyślnie zapisano dane wszystkich graczy!");
    }

    public boolean isDisabling() {
        return disabling;
    }
}
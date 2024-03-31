package io.github.flamehub.economy;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.argument.ArgumentKey;
import dev.rollczi.litecommands.bukkit.LiteCommandsBukkit;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import io.github.flamehub.commons.bukkit.BukkitPlugin;
import io.github.flamehub.commons.bukkit.command.argument.LocationArgument;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.network.player.NetworkPlayerArgument;
import io.github.flamehub.commons.bukkit.user.UserDatabaseListener;
import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.economy.user.*;
import io.github.flamehub.economy.user.updater.EconomyUserUpdateHandler;
import io.github.flamehub.economy.user.updater.EconomyUserUpdater;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.scheduler.BukkitScheduler;

public final class EconomyModule extends BukkitPlugin {

    private DatabaseConnector databaseConnector;
    private RedisMessenger redisMessenger;
    private BukkitMessagesService messagesService;

    private NetworkMessageService networkMessageService;

    private NetworkServerCache networkServerCache;
    private NetworkPlayerCache networkPlayerCache;

    private EconomyUserRepository economyUserRepository;
    private EconomyUserFactory economyUserFactory;
    private EconomyUserCache economyUserCache;
    private EconomyUserSaver economyUserSaver;
    private EconomyUserUpdater economyUserUpdater;

    @Override
    public void onEnable() {

        this.databaseConnector = getService(DatabaseConnector.class);
        this.messagesService = getService(BukkitMessagesService.class);
        this.networkPlayerCache = getService(NetworkPlayerCache.class);
        this.redisMessenger = getService(RedisMessenger.class);
        this.networkServerCache = getService(NetworkServerCache.class);
        this.networkMessageService = new NetworkMessageService(this.redisMessenger, "network_messages");

        this.economyUserRepository = new EconomyUserRepository(
                DatastoreFactory.create(
                        this.databaseConnector.getMongoClient(),
                        this.networkServerCache.getCurrent().getCategory(),
                        EconomyUser.class
                ),
                EconomyUser.class
        );
        this.economyUserFactory = new EconomyUserFactory();
        this.economyUserCache = new EconomyUserCache(this.economyUserRepository);
        this.economyUserSaver = new EconomyUserSaver(
                this.economyUserRepository,
                this.economyUserCache
        );
        this.economyUserUpdater = new EconomyUserUpdater(
                this.networkPlayerCache,
                this.economyUserRepository,
                this.redisMessenger
        );

        this.redisMessenger.subscribe(
                this.networkServerCache.getCurrent().getName(),
                new EconomyUserUpdateHandler(this.economyUserCache, this.economyUserRepository)
        );

        BukkitScheduler scheduler = this.getServer().getScheduler();
        scheduler.runTaskTimerAsynchronously(this, this.economyUserSaver, 0L, 20 * 60L);

        setupEconomy();
        setupListeners();
        setupCommands();
        setupPlaceholders();

    }

    @Override
    public void onDisable() {
        this.economyUserSaver.run();
    }

    void setupPlaceholders() {
        new EconomyPlaceholder(this.economyUserCache).register();
    }

    void setupEconomy() {
        ServicesManager servicesManager = getServer().getServicesManager();
        servicesManager.register(Economy.class,
                new EconomyVaultProvider(
                        this.getFlameDispatcher(),
                        redisMessenger,
                        this.economyUserCache,
                        this.economyUserRepository,
                        economyUserUpdater,
                        networkPlayerCache,
                        networkServerCache
                ),
                this, ServicePriority.Normal
        );

    }

    void setupListeners() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(
                new UserDatabaseListener<>(
                        this.getFlameDispatcher(),
                        pluginManager,
                        this.economyUserCache,
                        this.economyUserRepository,
                        this.economyUserFactory
                ),
                this
        );
    }

    void setupCommands() {
        LiteCommandsBukkit.builder()
                .settings(settings -> settings
                        .fallbackPrefix("economy")
                        .nativePermissions(false)
                )
                .argument(Location.class, new LocationArgument())
                .argument(Player.class, new PlayerArgument(this.messagesService))
                .argument(NetworkPlayer.class, new NetworkPlayerArgument(this.messagesService, this.networkPlayerCache, this.networkServerCache))
                .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

                .missingPermission(new MissingPermissionHandlerImpl(this.messagesService))
                .invalidUsage(new InvalidUsageHandlerImpl(this.messagesService))

                .commands(LiteCommandsAnnotations.of(
                        new BalanceCommand(this.economyUserCache, this.messagesService),
                        new EconomyCommand(this.messagesService, this.economyUserCache, this.economyUserUpdater, economyUserRepository),
                        new PayCommand(this.messagesService, this.economyUserCache, this.networkMessageService)
                ))
                .argumentSuggester(String.class, ArgumentKey.of("playerName"), (invocation, argument, context) -> Bukkit.getOnlinePlayers()
                        .stream()
                        .map(Player::getName)
                        .collect(SuggestionResult.collector())
                )
                .schematicGenerator(SchematicFormat.angleBrackets())
                .build();
    }

}
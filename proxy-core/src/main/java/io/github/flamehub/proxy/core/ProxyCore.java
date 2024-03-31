package io.github.flamehub.proxy.core;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.Scheduler;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import dev.rollczi.litecommands.velocity.LiteVelocityFactory;
import dev.rollczi.litecommands.velocity.tools.VelocityOnlyPlayerContextual;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.json.gson.JsonGsonConfigurer;
import io.github.flamehub.commons.database.DatabaseConfig;
import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.message.MessagesRepository;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.network.player.NetworkPlayerHandler;
import io.github.flamehub.proxy.core.auth.AuthListener;
import io.github.flamehub.proxy.core.auth.AuthLobbyConnector;
import io.github.flamehub.proxy.core.auth.AuthTask;
import io.github.flamehub.proxy.core.auth.command.AuthCommand;
import io.github.flamehub.proxy.core.auth.command.ChangePasswordCommand;
import io.github.flamehub.proxy.core.auth.command.LoginCommand;
import io.github.flamehub.proxy.core.auth.command.RegisterCommand;
import io.github.flamehub.proxy.core.auth.user.AuthUser;
import io.github.flamehub.proxy.core.auth.user.AuthUserCache;
import io.github.flamehub.proxy.core.auth.user.AuthUserRepository;
import io.github.flamehub.proxy.core.auth.user.update.AuthUserUpdater;
import io.github.flamehub.proxy.core.command.LobbyCommand;
import io.github.flamehub.proxy.core.command.argument.PlayerArgument;
import io.github.flamehub.proxy.core.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.proxy.core.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.proxy.core.locale.VelocityMessagesService;
import io.github.flamehub.proxy.core.motd.MotdCommand;
import io.github.flamehub.proxy.core.motd.MotdConfig;
import io.github.flamehub.proxy.core.motd.MotdListener;
import io.github.flamehub.proxy.core.player.PlayerPacketHandler;
import io.github.flamehub.proxy.core.player.network.NetworkPlayerGhostRemover;
import io.github.flamehub.proxy.core.player.network.NetworkPlayerListener;
import io.github.flamehub.proxy.core.queue.*;
import io.github.flamehub.proxy.core.redirect.RedirectHandler;
import io.github.flamehub.proxy.core.server.NetworkServerUpdateTask;
import io.github.flamehub.proxy.core.version.PlayerVersionListener;
import io.github.flamehub.commons.redis.RedisConfig;
import io.github.flamehub.commons.redis.RedisService;
import io.github.flamehub.commons.server.*;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Plugin(id = "proxy-core", version = "0.1")
public final class ProxyCore {

    private static ProxyCore instance;

    private final ProxyServer proxyServer;
    private final Logger logger;
    private final Path configDirectory;

    private DatabaseConfig databaseConfig;
    private DatabaseConnector databaseConnector;

    private RedisConfig redisConfig;
    private RedisService redisService;
    private RedisMessenger redisMessenger;

    private NetworkServerConfig networkServerConfig;
    private NetworkServerRepository networkServerRepository;
    private NetworkServerCache networkServerCache;
    private NetworkServerLoader networkServerLoader;
    private NetworkPlayerCache networkPlayerCache;

    private VelocityMessagesService messagesService;
    private MessagesRepository messagesRepository;
    private MotdConfig motdConfig;

    private AuthUserCache authUserCache;
    private AuthUserRepository authUserRepository;
    private AuthUserUpdater authUserUpdater;
    private AuthLobbyConnector authLobbyConnector;

    private QueueService queueService;
    private QueueRedirectService queueRedirectService;

    @Inject
    public ProxyCore(ProxyServer proxyServer, Logger logger, @DataDirectory Path configDirectory) {
        instance = this;
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.configDirectory = configDirectory;
    }

    public static ProxyCore getInstance() {
        return instance;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        setupConfigurations();

        this.databaseConnector = new DatabaseConnector(this.databaseConfig.getMongoUri());
        this.redisService = new RedisService(this.redisConfig.getHost(), this.redisConfig.getPassword(), this.redisConfig.getPort());
        this.redisMessenger = new RedisMessenger(this.redisService.getClient());
        this.redisMessenger.subscribeCallbacks("callbacks");

        this.networkServerCache = new NetworkServerCache();
        this.networkServerRepository = new NetworkServerRepository(
                DatastoreFactory.create(
                        this.databaseConnector.getMongoClient(),
                        "global",
                        NetworkServer.class,
                        NetworkServerStatistics.class
                ),
                NetworkServer.class
        );
        this.networkServerLoader = new NetworkServerLoader(
                this.logger,
                this.networkServerCache,
                this.networkServerRepository,
                this.networkServerConfig.getCurrentServerName()
        );
        this.networkServerLoader.load();

        this.messagesService = new VelocityMessagesService();
        this.messagesRepository = new MessagesRepository(this.databaseConnector, this.messagesService);
        this.messagesRepository.loadMessages();

        this.networkPlayerCache = new NetworkPlayerCache(this.redisService, this.redisMessenger);
        this.networkPlayerCache.load();
        this.redisMessenger.subscribe("network_players", new NetworkPlayerHandler(this.networkPlayerCache));
        this.redisMessenger.subscribe("velocity_servers", new PlayerPacketHandler(this.proxyServer));
        this.redisMessenger.subscribe("network_servers", new NetworkServerUpdateHandler(this.logger, this.networkServerCache));
        this.redisMessenger.subscribe("redirect", new RedirectHandler(this.proxyServer));

        this.authUserRepository = new AuthUserRepository(DatastoreFactory.create(this.databaseConnector.getMongoClient(), "global", AuthUser.class), AuthUser.class);
        this.authUserCache = new AuthUserCache(this.authUserRepository);
        this.authUserUpdater = new AuthUserUpdater(this.networkPlayerCache, this.authUserRepository, this.redisMessenger);
        this.authLobbyConnector = new AuthLobbyConnector(this.proxyServer, this.networkServerCache, this.messagesService);

        this.queueService = new QueueService();
        this.queueRedirectService = new QueueRedirectService(this.proxyServer, this.queueService, this.networkServerCache);
        this.redisMessenger.subscribe("queue", new QueueHandler(this.proxyServer, this.queueService));

        setupTasks();
        setupEvents();
        setupCommands();


    }

    void setupTasks() {
        Scheduler scheduler = this.proxyServer.getScheduler();
        scheduler.buildTask(this, new AuthTask(this.proxyServer, this.authUserCache))
                .repeat(500, TimeUnit.MILLISECONDS)
                .schedule();

        scheduler.buildTask(this, new QueueInfoTask(this.proxyServer, this.queueService))
                    .repeat(500L, TimeUnit.MILLISECONDS)
                    .schedule();

        scheduler.buildTask(this, new QueueRedirectTask(this.queueService, this.queueRedirectService))
                    .repeat(2L, TimeUnit.SECONDS)
                    .schedule();

        scheduler.buildTask(this, new NetworkServerUpdateTask(this.proxyServer, this.redisMessenger, this.networkServerCache))
                .repeat(1, TimeUnit.SECONDS)
                .schedule();

        scheduler.buildTask(this, new NetworkPlayerGhostRemover(this.logger, this.proxyServer, this.networkServerCache, this.networkPlayerCache))
                .repeat(30, TimeUnit.SECONDS)
                .schedule();
    }

    void setupConfigurations() {
        this.databaseConfig = ConfigManager.create(DatabaseConfig.class, (it) -> {
            it.withConfigurer(new JsonGsonConfigurer());
            it.withBindFile(this.configDirectory + "/database.json");
            it.saveDefaults();
            it.load(true);
        });

        this.redisConfig = ConfigManager.create(RedisConfig.class, (it) -> {
            it.withConfigurer(new JsonGsonConfigurer());
            it.withBindFile(configDirectory + "/redis.json");
            it.saveDefaults();
            it.load(true);
        });

        this.networkServerConfig = ConfigManager.create(NetworkServerConfig.class, (it) -> {
            it.withConfigurer(new JsonGsonConfigurer());
            it.withBindFile(this.configDirectory + "/networkServer.json");
            it.saveDefaults();
            it.load(true);
        });

        this.motdConfig = ConfigManager.create(MotdConfig.class, (it) -> {
            it.withConfigurer(new JsonGsonConfigurer());
            it.withBindFile(this.configDirectory + "/motd.json");
            it.saveDefaults();
            it.load(true);
        });

    }

    void setupEvents() {
        EventManager eventManager = this.proxyServer.getEventManager();
        eventManager.register(this, new MotdListener(this.motdConfig, this.proxyServer, this.networkServerCache));
        eventManager.register(this, new NetworkPlayerListener(this.networkServerCache, this.networkPlayerCache));
        eventManager.register(this, new PlayerVersionListener(this.messagesService));
        eventManager.register(this, new QueueListener(networkServerCache, queueService, proxyServer));
        eventManager.register(this, new AuthListener(
                proxyServer, this.networkPlayerCache,
                this.authUserCache,
                this.authUserRepository,
                this.messagesService,
                this.authLobbyConnector
        ));
    }

    void setupCommands() {
        LiteVelocityFactory.builder(this.getProxyServer())
                .settings(settings -> settings
                        .nativePermissions(false)
                )
                .argument(Player.class, new PlayerArgument(this.proxyServer, this.messagesService))
                .context(Player.class, new VelocityOnlyPlayerContextual<>("&cOnly player can execute this command!"))

                .missingPermission(new MissingPermissionHandlerImpl(this.messagesService))
                .invalidUsage(new InvalidUsageHandlerImpl(this.messagesService))

                .commands(LiteCommandsAnnotations.of(
                        new MotdCommand(this.motdConfig),
                        new AuthCommand(this.authUserUpdater, this.authUserCache, this.messagesService, this.redisMessenger),
                        new LoginCommand(this.authUserCache, this.authLobbyConnector),
                        new RegisterCommand(this.authUserCache, this.authUserRepository, this.authLobbyConnector),
                        new ChangePasswordCommand(this.authUserCache, this.authUserRepository),
                        new LobbyCommand(this.proxyServer, this.networkServerCache, this.messagesService, authUserCache),
                        new QueueCommand(proxyServer, this.networkServerCache, this.queueService, queueRedirectService)
                ))

                .schematicGenerator(SchematicFormat.angleBrackets())
                .build();
    }

    public ProxyServer getProxyServer() {
        return proxyServer;
    }

    public Logger getLogger() {
        return logger;
    }

    public Path getConfigDirectory() {
        return configDirectory;
    }

    public DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }

    public DatabaseConnector getDatabaseConnector() {
        return databaseConnector;
    }

    public RedisConfig getRedisConfig() {
        return redisConfig;
    }

    public RedisService getRedisService() {
        return redisService;
    }

    public RedisMessenger getRedisMessenger() {
        return redisMessenger;
    }

    public NetworkServerConfig getNetworkServerConfig() {
        return networkServerConfig;
    }

    public NetworkServerRepository getNetworkServerRepository() {
        return networkServerRepository;
    }

    public NetworkServerCache getNetworkServerCache() {
        return networkServerCache;
    }

    public NetworkServerLoader getNetworkServerLoader() {
        return networkServerLoader;
    }

    public NetworkPlayerCache getNetworkPlayerCache() {
        return networkPlayerCache;
    }

    public VelocityMessagesService getMessagesService() {
        return messagesService;
    }

    public MessagesRepository getLocaleRepository() {
        return messagesRepository;
    }

    public MotdConfig getMotdConfig() {
        return motdConfig;
    }
}

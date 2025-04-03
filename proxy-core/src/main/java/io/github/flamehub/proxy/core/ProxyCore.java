package io.github.flamehub.proxy.core;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.Scheduler;
import dev.morphia.Datastore;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.argument.ArgumentKey;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import dev.rollczi.litecommands.velocity.LiteVelocityFactory;
import dev.rollczi.litecommands.velocity.tools.VelocityOnlyPlayerContextual;
import io.github.flamehub.commons.config.FlameConfigService;
import io.github.flamehub.commons.config.RemoteRepository;
import io.github.flamehub.commons.config.RemoteUpdateHandler;
import io.github.flamehub.commons.config.serializer.FlameConfigSerializer;
import io.github.flamehub.commons.config.serializer.FlameJacksonConfigSerializer;
import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.network.player.NetworkPlayerHandler;
import io.github.flamehub.commons.property.PropertyLoader;
import io.github.flamehub.commons.punishment.Punishment;
import io.github.flamehub.commons.punishment.PunishmentMessages;
import io.github.flamehub.commons.punishment.PunishmentRepository;
import io.github.flamehub.commons.redis.RedisService;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.commons.server.NetworkServerLoader;
import io.github.flamehub.commons.server.NetworkServerRepository;
import io.github.flamehub.commons.server.NetworkServerUpdateHandler;
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
import io.github.flamehub.proxy.core.blacklist.Blacklist;
import io.github.flamehub.proxy.core.blacklist.BlacklistCommand;
import io.github.flamehub.proxy.core.blacklist.BlacklistListener;
import io.github.flamehub.proxy.core.blacklist.BlacklistRepository;
import io.github.flamehub.proxy.core.command.LobbyCommand;
import io.github.flamehub.proxy.core.command.argument.PlayerArgument;
import io.github.flamehub.proxy.core.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.proxy.core.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.proxy.core.motd.MotdCommand;
import io.github.flamehub.proxy.core.motd.MotdConfig;
import io.github.flamehub.proxy.core.motd.MotdListener;
import io.github.flamehub.proxy.core.player.PlayerPacketHandler;
import io.github.flamehub.proxy.core.player.network.NetworkPlayerGhostRemover;
import io.github.flamehub.proxy.core.player.network.NetworkPlayerListener;
import io.github.flamehub.proxy.core.punishment.PunishmentHandler;
import io.github.flamehub.proxy.core.queue.QueueCommand;
import io.github.flamehub.proxy.core.queue.QueueConfig;
import io.github.flamehub.proxy.core.queue.QueueHandler;
import io.github.flamehub.proxy.core.queue.QueueListener;
import io.github.flamehub.proxy.core.queue.QueuePositionTask;
import io.github.flamehub.proxy.core.queue.QueueRedirectService;
import io.github.flamehub.proxy.core.queue.QueueRedirectTask;
import io.github.flamehub.proxy.core.queue.QueueService;
import io.github.flamehub.proxy.core.redirect.RedirectHandler;
import io.github.flamehub.proxy.core.server.NetworkServerUpdateTask;
import io.github.flamehub.proxy.core.version.PlayerVersionListener;
import io.github.flamehub.proxy.core.vpn.VPNEntry;
import io.github.flamehub.proxy.core.vpn.VPNEntryRepository;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

@Plugin(id = "proxy-core", version = "0.1")
public final class ProxyCore {

  private final ProxyServer proxyServer;
  private final Logger logger;
  private final Path configDirectory;

  private ProxyMessages proxyMessages;

  private DatabaseConnector databaseConnector;
  private RedisService redisService;
  private RedisMessenger redisMessenger;

  private NetworkServerRepository networkServerRepository;
  private NetworkServerCache networkServerCache;
  private NetworkServerLoader networkServerLoader;
  private NetworkPlayerCache networkPlayerCache;

  private MotdConfig motdConfig;

  private AuthUserCache authUserCache;
  private AuthUserRepository authUserRepository;
  private AuthLobbyConnector authLobbyConnector;

  private QueueConfig queueConfig;
  private QueueService queueService;
  private QueueRedirectService queueRedirectService;

  private VPNEntryRepository vpnEntryRepository;
  private PunishmentRepository punishmentRepository;
  private PunishmentMessages punishmentMessages;

  private FlameConfigService flameConfigService;
  private RemoteRepository remoteRepository;

  private BlacklistRepository blacklistRepository;

  @Inject
  public ProxyCore(ProxyServer proxyServer, Logger logger, @DataDirectory Path configDirectory) {
    this.proxyServer = proxyServer;
    this.logger = logger;
    this.configDirectory = configDirectory;
  }

  @Subscribe
  public void onProxyInitialize(ProxyInitializeEvent event) {

    saveResource("credentials.properties", false);
    saveResource("network.properties", false);

    final PropertyLoader networkProperties = new PropertyLoader(
        configDirectory.toFile() + "/network.properties"
    );
    final String currentServerName = networkProperties.getProperty("current.server");

    final PropertyLoader credentialsProperties = new PropertyLoader(
        configDirectory.toFile() + "/credentials.properties"
    );
    this.databaseConnector = new DatabaseConnector(credentialsProperties.getProperty("mongo.uri"));
    this.redisService = new RedisService(
        credentialsProperties.getProperty("redis.host"),
        credentialsProperties.getProperty("redis.password"),
        Integer.parseInt(credentialsProperties.getProperty("redis.port"))
    );
    this.redisMessenger = new RedisMessenger(redisService.getClient());
    redisMessenger.subscribeCallbacks("callbacks");

    Datastore global = DatastoreFactory.create(
        databaseConnector.getMongoClient(),
        "global",
        AuthUser.class,
        NetworkServer.class,
        VPNEntry.class,
        Punishment.class,
        Blacklist.class
    );
    this.networkServerCache = new NetworkServerCache();
    this.networkServerRepository = new NetworkServerRepository(global, NetworkServer.class);
    this.networkServerLoader = new NetworkServerLoader(
        logger,
        networkServerCache,
        networkServerRepository,
        currentServerName
    );
    networkServerLoader.load();

    final ObjectMapper mapper = JsonMapper.builder()
        .enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
        .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build();
    mapper.getSerializationConfig().getDefaultVisibilityChecker()
        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
        .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
        .withCreatorVisibility(JsonAutoDetect.Visibility.NONE);
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
    prettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
    mapper.setDefaultPrettyPrinter(prettyPrinter);

    final FlameConfigSerializer flameConfigSerializer = new FlameJacksonConfigSerializer(mapper);
    this.remoteRepository = new RemoteRepository(
        flameConfigSerializer,
        databaseConnector.getMongoClient(),
        networkServerCache.getCurrent().getCategory()
    );
    this.flameConfigService = new FlameConfigService(
        redisMessenger,
        remoteRepository,
        flameConfigSerializer,
        networkServerCache.getCurrent().getCategory() + "_config_update"
    );
    redisMessenger.subscribe(flameConfigService.getRemoteConfigUpdateChannel(),
        new RemoteUpdateHandler(flameConfigService));
    setupConfigurations();

    this.networkPlayerCache = new NetworkPlayerCache(redisService, redisMessenger);
    networkPlayerCache.load();
    redisMessenger.subscribe("network_players",
        new NetworkPlayerHandler(networkPlayerCache));
    redisMessenger.subscribe("velocity_servers", new PlayerPacketHandler(proxyServer));
    redisMessenger.subscribe("network_servers",
        new NetworkServerUpdateHandler(logger, networkServerCache));
    redisMessenger.subscribe("redirect", new RedirectHandler(proxyServer));
    redisMessenger.subscribe("punishments",
        new PunishmentHandler(proxyServer));

    this.authUserRepository = new AuthUserRepository(global);
    this.authUserCache = new AuthUserCache(redisMessenger, redisService, authUserRepository);
    this.authLobbyConnector = new AuthLobbyConnector(proxyServer, networkServerCache,
        proxyMessages);

    this.vpnEntryRepository = new VPNEntryRepository(global);
    this.punishmentRepository = new PunishmentRepository(global);

    this.queueService = new QueueService();
    this.queueRedirectService = new QueueRedirectService(proxyServer, queueService,
        networkServerCache, punishmentRepository, punishmentMessages);
    redisMessenger.subscribe("queue", new QueueHandler(proxyServer, queueService));

    this.blacklistRepository = new BlacklistRepository(global);

    setupTasks();
    setupEvents();
    setupCommands();


  }

  void setupTasks() {
    Scheduler scheduler = proxyServer.getScheduler();
    scheduler.buildTask(this, new AuthTask(proxyServer, authUserCache))
        .repeat(500, TimeUnit.MILLISECONDS)
        .schedule();

    scheduler.buildTask(this, new QueuePositionTask(proxyServer, queueService))
        .repeat(500L, TimeUnit.MILLISECONDS)
        .schedule();

    scheduler.buildTask(this,
            new QueueRedirectTask(queueConfig, queueService, queueRedirectService))
        .repeat(100L, TimeUnit.MILLISECONDS)
        .schedule();

    scheduler.buildTask(this,
            new NetworkServerUpdateTask(proxyServer, redisMessenger, networkServerCache))
        .repeat(1, TimeUnit.SECONDS)
        .schedule();

    scheduler.buildTask(this,
            new NetworkPlayerGhostRemover(logger, proxyServer, networkServerCache,
                networkPlayerCache))
        .repeat(30, TimeUnit.SECONDS)
        .schedule();
  }

  void setupConfigurations() {

    this.proxyMessages = flameConfigService.getOrCreate(configDirectory.toFile(),
        ProxyMessages.class);

    this.punishmentMessages = flameConfigService.getOrCreate(configDirectory.toFile(),
        PunishmentMessages.class);

    this.motdConfig = flameConfigService.getOrCreate(configDirectory.toFile(),
        MotdConfig.class);

    this.queueConfig = flameConfigService.getOrCreate(configDirectory.toFile(),
        QueueConfig.class);


  }

  void setupEvents() {
    EventManager eventManager = proxyServer.getEventManager();
    eventManager.register(this,
        new MotdListener(motdConfig, proxyServer, networkServerCache, networkPlayerCache));
    eventManager.register(this,
        new NetworkPlayerListener(networkServerCache, networkPlayerCache));
    eventManager.register(this, new PlayerVersionListener(proxyMessages));
    eventManager.register(this,
        new QueueListener(networkServerCache, queueService, proxyServer, this));
    eventManager.register(this, new AuthListener(
        this,
        proxyServer,
        proxyMessages,
        networkPlayerCache,
        authUserCache,
        authUserRepository,
        authLobbyConnector,
        vpnEntryRepository));
    eventManager.register(this, new BlacklistListener(blacklistRepository, proxyMessages));
  }

  void setupCommands() {
    LiteVelocityFactory.builder(proxyServer)
        .settings(settings -> settings
            .nativePermissions(false)
        )
        .argument(Player.class, new PlayerArgument(proxyServer, proxyMessages))
        .context(Player.class,
            new VelocityOnlyPlayerContextual<>("&cOnly player can execute this command!"))

        .missingPermission(new MissingPermissionHandlerImpl(proxyMessages))
        .invalidUsage(new InvalidUsageHandlerImpl(proxyMessages))

        .commands(LiteCommandsAnnotations.of(
            new MotdCommand(flameConfigService),
            new AuthCommand(proxyServer, proxyMessages, authUserRepository, authUserCache, redisMessenger),
            new LoginCommand(proxyMessages, authUserCache, authLobbyConnector),
            new RegisterCommand(proxyMessages, authUserCache, authLobbyConnector),
            new ChangePasswordCommand(authUserCache, proxyMessages),
            new LobbyCommand(proxyServer, proxyMessages, networkServerCache),
            new QueueCommand(proxyServer, flameConfigService, queueConfig, queueService,
                networkServerCache,
                queueRedirectService),
            new BlacklistCommand(proxyServer, blacklistRepository, proxyMessages)
        ))
        .argumentSuggester(String.class, ArgumentKey.of("networkPlayer"),
            (invocation, argument, context) -> networkPlayerCache.values()
                .stream()
                .map(NetworkPlayer::getName)
                .collect(SuggestionResult.collector())
        )
        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();
  }

  public void saveResource(@NotNull String resourcePath, boolean replace) {
    if (resourcePath == null || resourcePath.equals("")) {
      throw new IllegalArgumentException("ResourcePath cannot be null or empty");
    }

    resourcePath = resourcePath.replace('\\', '/');
    InputStream in = getResource(resourcePath);
    if (in == null) {
      throw new IllegalArgumentException(
          "The embedded resource '" + resourcePath + "' cannot be found");
    }

    File outFile = new File(configDirectory.toFile(), resourcePath);
    int lastIndex = resourcePath.lastIndexOf('/');
    File outDir = new File(configDirectory.toFile(),
        resourcePath.substring(0, Math.max(lastIndex, 0)));

    if (!outDir.exists()) {
      outDir.mkdirs();
    }

    try {
      if (!outFile.exists() || replace) {
        OutputStream out = new FileOutputStream(outFile);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
          out.write(buf, 0, len);
        }
        out.close();
        in.close();
      } else {
        logger.log(Level.WARNING,
            "Could not save " + outFile.getName() + " to " + outFile + " because "
                + outFile.getName() + " already exists.");
      }
    } catch (IOException ex) {
      logger.log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
    }
  }

  public InputStream getResource(@NotNull String filename) {

    try {
      URL url = getClass().getClassLoader().getResource(filename);

      if (url == null) {
        return null;
      }

      URLConnection connection = url.openConnection();
      connection.setUseCaches(false);
      return connection.getInputStream();
    } catch (IOException ex) {
      return null;
    }
  }

  public ProxyServer getProxyServer() {
    return proxyServer;
  }

}

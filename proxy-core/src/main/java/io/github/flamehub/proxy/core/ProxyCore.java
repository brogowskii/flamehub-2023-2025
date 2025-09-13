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
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
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
import io.github.flamehub.commons.Credentials;
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
import io.github.flamehub.commons.punishment.Punishment;
import io.github.flamehub.commons.punishment.PunishmentMessages;
import io.github.flamehub.commons.punishment.PunishmentRepository;
import io.github.flamehub.commons.redis.RedisService;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerConfigurator;
import io.github.flamehub.commons.server.NetworkServerContext;
import io.github.flamehub.commons.server.NetworkServerFacade;
import io.github.flamehub.commons.server.NetworkServerSettings;
import io.github.flamehub.commons.server.NetworkServerStatistics;
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
import io.github.flamehub.proxy.core.server.NetworkServerRegisterTask;
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

  private NetworkServerFacade networkServerFacade;
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
  public ProxyCore(final ProxyServer proxyServer, final Logger logger,
      @DataDirectory final Path configDirectory) {
    this.proxyServer = proxyServer;
    this.logger = logger;
    this.configDirectory = configDirectory;
  }


  @Subscribe
  public void onProxyShutdown(ProxyShutdownEvent event) {
    final NetworkServer current = networkServerFacade.getCurrent();
    networkServerFacade.remove(current.getName());
  }


  @Subscribe
  public void onProxyInitialize(final ProxyInitializeEvent event) {
    databaseConnector = new DatabaseConnector(Credentials.MONG0_CREDENTIALS);
    redisService = new RedisService(
        Credentials.REDIS_HOST,
        Credentials.REDIS_PASSWORD,
        Credentials.REDIS_PORT,
        ProxyCore.class.getClassLoader()
    );
    redisMessenger = new RedisMessenger(redisService.getClient());
    redisMessenger.subscribeCallbacks("callbacks");

    final Datastore global = DatastoreFactory.create(
        databaseConnector.getMongoClient(),
        "global",
        AuthUser.class,
        VPNEntry.class,
        Punishment.class,
        Blacklist.class
    );

    final NetworkServer current = new NetworkServer(
        NetworkServerContext.CURRENT_NAME,
        NetworkServerContext.CURRENT_CATEGORY,
        "0.0.0.0",
        new NetworkServerStatistics()
    );
    networkServerFacade = new NetworkServerConfigurator().networkServerFacade(
        redisMessenger,
        redisService,
        current
    );

    networkServerFacade.put(NetworkServerContext.CURRENT_NAME, current);
    networkServerFacade.putSetting(NetworkServerContext.CURRENT_NAME, new NetworkServerSettings());

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
    final DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
    prettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
    mapper.setDefaultPrettyPrinter(prettyPrinter);

    final FlameConfigSerializer flameConfigSerializer = new FlameJacksonConfigSerializer(mapper);
    remoteRepository = new RemoteRepository(
        flameConfigSerializer,
        databaseConnector.getMongoClient(),
        networkServerFacade.getCurrent().getCategory()
    );
    flameConfigService = new FlameConfigService(
        redisMessenger,
        remoteRepository,
        networkServerFacade.getCurrent().getCategory() + "_config_update"
    );
    redisMessenger.subscribe(flameConfigService.getRemoteConfigUpdateChannel(),
        new RemoteUpdateHandler(flameConfigService));
    setupConfigurations();

    networkPlayerCache = new NetworkPlayerCache(redisService, redisMessenger);
    networkPlayerCache.load();
    redisMessenger.subscribe("network_players",
        new NetworkPlayerHandler(networkPlayerCache));
    redisMessenger.subscribe("velocity_servers", new PlayerPacketHandler(proxyServer));
    redisMessenger.subscribe("redirect", new RedirectHandler(proxyServer));
    redisMessenger.subscribe("punishments",
        new PunishmentHandler(proxyServer));

    authUserRepository = new AuthUserRepository(global);
    authUserCache = new AuthUserCache(redisMessenger, redisService, authUserRepository);
    authLobbyConnector = new AuthLobbyConnector(proxyServer, networkServerFacade,
        proxyMessages);

    vpnEntryRepository = new VPNEntryRepository(global);
    punishmentRepository = new PunishmentRepository(global);

    queueService = new QueueService();
    queueRedirectService = new QueueRedirectService(proxyServer, queueService,
        networkServerFacade, punishmentRepository, punishmentMessages);
    redisMessenger.subscribe("queue", new QueueHandler(proxyServer, queueService));

    blacklistRepository = new BlacklistRepository(global);

    setupTasks();
    setupEvents();
    setupCommands();


  }

  void setupTasks() {
    final Scheduler scheduler = proxyServer.getScheduler();
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
            new NetworkServerUpdateTask(networkServerFacade, proxyServer))
        .repeat(1, TimeUnit.SECONDS)
        .schedule();

    scheduler.buildTask(this,
            new NetworkPlayerGhostRemover(logger, proxyServer, networkServerFacade, networkPlayerCache))
        .repeat(5, TimeUnit.MINUTES)
        .schedule();

    scheduler.buildTask(this,
            new NetworkServerRegisterTask(proxyServer, networkServerFacade))
        .repeat(1, TimeUnit.SECONDS)
        .schedule();
  }

  void setupConfigurations() {

    proxyMessages = flameConfigService.getOrCreate(ProxyMessages.class);
    punishmentMessages = flameConfigService.getOrCreate(PunishmentMessages.class);
    motdConfig = flameConfigService.getOrCreate(MotdConfig.class);
    queueConfig = flameConfigService.getOrCreate(QueueConfig.class);

  }

  void setupEvents() {
    final EventManager eventManager = proxyServer.getEventManager();
    eventManager.register(this,
        new MotdListener(motdConfig, proxyServer, networkServerFacade, networkPlayerCache));
    eventManager.register(this,
        new NetworkPlayerListener(networkServerFacade, networkPlayerCache));
    eventManager.register(this, new PlayerVersionListener(proxyMessages));
    eventManager.register(this,
        new QueueListener(networkServerFacade, queueService, proxyServer, this));
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
            new AuthCommand(proxyServer, proxyMessages, authUserRepository, authUserCache,
                redisMessenger),
            new LoginCommand(proxyMessages, authUserCache, authLobbyConnector),
            new RegisterCommand(proxyMessages, authUserCache, authLobbyConnector),
            new ChangePasswordCommand(authUserCache, proxyMessages),
            new LobbyCommand(proxyServer, proxyMessages, networkServerFacade),
            new QueueCommand(proxyServer, flameConfigService, queueConfig, queueService,
                networkServerFacade,
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

  public void saveResource(@NotNull String resourcePath, final boolean replace) {
    if (resourcePath == null || "".equals(resourcePath)) {
      throw new IllegalArgumentException("ResourcePath cannot be null or empty");
    }

    resourcePath = resourcePath.replace('\\', '/');
    final InputStream in = getResource(resourcePath);
    if (in == null) {
      throw new IllegalArgumentException(
          "The embedded resource '" + resourcePath + "' cannot be found");
    }

    final File outFile = new File( resourcePath);
    final int lastIndex = resourcePath.lastIndexOf('/');
    final File outDir = new File(
        resourcePath.substring(0, Math.max(lastIndex, 0)));

    if (!outDir.exists()) {
      outDir.mkdirs();
    }

    try {
      if (!outFile.exists() || replace) {
        final OutputStream out = new FileOutputStream(outFile);
        final byte[] buf = new byte[1024];
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
    } catch (final IOException ex) {
      logger.log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
    }
  }

  public InputStream getResource(@NotNull final String filename) {

    try {
      final URL url = getClass().getClassLoader().getResource(filename);

      if (url == null) {
        return null;
      }

      final URLConnection connection = url.openConnection();
      connection.setUseCaches(false);
      return connection.getInputStream();
    } catch (final IOException ex) {
      return null;
    }
  }

  public ProxyServer getProxyServer() {
    return proxyServer;
  }

}

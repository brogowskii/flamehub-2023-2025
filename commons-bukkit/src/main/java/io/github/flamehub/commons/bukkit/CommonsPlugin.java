package io.github.flamehub.commons.bukkit;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.argument.ArgumentKey;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.cooldown.CooldownState;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import io.github.flamehub.commons.bukkit.actionbar.ActionBarTask;
import io.github.flamehub.commons.bukkit.automessage.AutoMessageConfig;
import io.github.flamehub.commons.bukkit.automessage.AutoMessageReloadCommand;
import io.github.flamehub.commons.bukkit.automessage.AutoMessageTask;
import io.github.flamehub.commons.bukkit.censure.CensureCommand;
import io.github.flamehub.commons.bukkit.censure.CensureConfig;
import io.github.flamehub.commons.bukkit.censure.CensureListener;
import io.github.flamehub.commons.bukkit.command.AdminChatCommand;
import io.github.flamehub.commons.bukkit.command.BroadcastCommand;
import io.github.flamehub.commons.bukkit.command.HelpopCommand;
import io.github.flamehub.commons.bukkit.command.argument.LocationArgument;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.handler.CooldownStateResultHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.bukkit.execute.ExecuteCommand;
import io.github.flamehub.commons.bukkit.execute.ExecuteHandler;
import io.github.flamehub.commons.bukkit.listener.PlayerJoinQuitListener;
import io.github.flamehub.commons.bukkit.listener.ProtectorListener;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.message.MessagesReloadCommand;
import io.github.flamehub.commons.bukkit.network.message.NetworkMessageHandler;
import io.github.flamehub.commons.bukkit.network.player.NetworkPlayerGhostRemoveCommand;
import io.github.flamehub.commons.bukkit.placeholder.PlayerPlaceholder;
import io.github.flamehub.commons.bukkit.punishment.PunishmentCommand;
import io.github.flamehub.commons.bukkit.punishment.PunishmentListener;
import io.github.flamehub.commons.bukkit.server.NetworkServerPlaceholder;
import io.github.flamehub.commons.bukkit.server.NetworkServerUpdateTask;
import io.github.flamehub.commons.bukkit.server.NetworkServersCommand;
import io.github.flamehub.commons.bukkit.spin.SpinGuiListener;
import io.github.flamehub.commons.bukkit.teleport.TeleporterService;
import io.github.flamehub.commons.bukkit.teleport.TeleporterTask;
import io.github.flamehub.commons.bukkit.util.JacksonAdapters;
import io.github.flamehub.commons.config.FlameConfigService;
import io.github.flamehub.commons.config.RemoteRepository;
import io.github.flamehub.commons.config.RemoteUpdateHandler;
import io.github.flamehub.commons.config.serializer.FlameConfigSerializer;
import io.github.flamehub.commons.config.serializer.FlameJacksonConfigSerializer;
import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.message.MessagesRepository;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.message.NetworkMessageService;
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
import io.github.flamehub.commons.server.NetworkServerStatistics;
import io.github.flamehub.commons.server.NetworkServerUpdate;
import io.github.flamehub.commons.server.NetworkServerUpdateHandler;
import io.github.flamehub.commons.util.JacksonPostHookDeserializer;
import java.time.Duration;
import java.time.Instant;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitScheduler;

public final class CommonsPlugin extends BukkitPlugin {

  private static CommonsPlugin instance;
  private RedisService redisService;
  private RedisMessenger redisMessenger;
  private DatabaseConnector databaseConnector;
  private NetworkMessageService networkMessageService;
  private NetworkPlayerCache networkPlayerCache;
  private NetworkServerCache networkServerCache;
  private NetworkServerLoader networkServerLoader;
  private NetworkServerRepository networkServerRepository;
  private MessagesRepository messagesRepository;
  private BukkitMessagesService messagesService;
  private AutoMessageConfig autoMessageConfig;
  private TeleporterService teleporterService;
  private CensureConfig censureConfig;
  private FlameConfigService flameConfigService;
  private RemoteRepository remoteRepository;
  private PunishmentRepository punishmentRepository;
  private PunishmentMessages punishmentMessages;

  public static CommonsPlugin getInstance() {
    return instance;
  }

  @Override
  public void onLoad() {
    instance = this;
  }

  @Override
  public void onEnable() {

    saveResource("credentials.properties", false);
    saveResource("network.properties", false);

    final PropertyLoader networkProperties = new PropertyLoader(
        getDataFolder() + "/network.properties"
    );
    final String currentServerName = networkProperties.getProperty("current.server");

    final PropertyLoader credentialsProperties = new PropertyLoader(
        getDataFolder() + "/credentials.properties"
    );
    this.databaseConnector = new DatabaseConnector(credentialsProperties.getProperty("mongo.uri"));
    this.redisService = new RedisService(
        credentialsProperties.getProperty("redis.host"),
        credentialsProperties.getProperty("redis.password"),
        Integer.parseInt(credentialsProperties.getProperty("redis.port"))
    );
    this.redisMessenger = new RedisMessenger(redisService.getClient());
    redisMessenger.subscribeCallbacks("callbacks");

    this.networkServerCache = new NetworkServerCache();
    this.networkServerRepository = new NetworkServerRepository(
        DatastoreFactory.create(
            databaseConnector.getMongoClient(),
            "global",
            NetworkServer.class,
            NetworkServerStatistics.class
        ),
        NetworkServer.class
    );
    this.networkServerLoader = new NetworkServerLoader(
        getLogger(),
        networkServerCache,
        networkServerRepository,
        currentServerName
    );
    networkServerLoader.load();

    final ObjectMapper mapper = JsonMapper.builder()
        .enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
        .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
        .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
        .enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
        .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build();
    mapper.getSerializationConfig().getDefaultVisibilityChecker()
        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
        .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
        .withCreatorVisibility(JsonAutoDetect.Visibility.NONE);

    final SimpleModule simpleModule = new SimpleModule();
    simpleModule.addSerializer(ItemStack.class, new JacksonAdapters.ItemStackSerializer());
    simpleModule.addSerializer(Location.class, new JacksonAdapters.LocationSerializer());
    simpleModule.addSerializer(PotionEffect.class, new JacksonAdapters.PotionEffectSerializer());
    simpleModule.addDeserializer(ItemStack.class, new JacksonAdapters.ItemStackDeserializer());
    simpleModule.addDeserializer(Location.class, new JacksonAdapters.LocationDeserializer());
    simpleModule.addDeserializer(PotionEffect.class,
        new JacksonAdapters.PotionEffectDeserializer());
    simpleModule.addSerializer(Duration.class, new JacksonAdapters.DurationSerializer());
    simpleModule.addDeserializer(Duration.class, new JacksonAdapters.DurationDeserializer());
    simpleModule.addSerializer(Instant.class, new JacksonAdapters.InstantSerializer());
    simpleModule.addDeserializer(Instant.class, new JacksonAdapters.InstantDeserializer());

    mapper.registerModule(simpleModule);
    mapper.registerModule(JacksonPostHookDeserializer.getSimpleModule());
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

    this.networkMessageService = new NetworkMessageService(redisMessenger, "network_messages");

    this.messagesService = new BukkitMessagesService();
    this.messagesRepository = new MessagesRepository(databaseConnector, messagesService);
    messagesRepository.loadMessages();

    redisMessenger.subscribe("network_servers",
        new NetworkServerUpdateHandler(getLogger(), networkServerCache));
    redisMessenger.subscribe("network_messages",
        new NetworkMessageHandler(networkServerCache));
    redisMessenger.subscribe("network_players",
        new NetworkPlayerHandler(networkPlayerCache));
    redisMessenger.subscribe(networkServerCache.getCurrent().getCategory(),
        new ExecuteHandler(flameDispatcher));

    this.teleporterService = new TeleporterService();
    this.punishmentRepository = new PunishmentRepository(
        DatastoreFactory.create(databaseConnector.getMongoClient(), "global",
            Punishment.class));

    setupServices();
    setupCommands();
    setupPlaceholders();
    setupTasks();
    setupListeners();

  }

  void setupListeners() {
    final PluginManager pluginManager = getServer().getPluginManager();
    pluginManager.registerEvents(new PlayerJoinQuitListener(), this);
    pluginManager.registerEvents(new ProtectorListener(messagesService), this);
    pluginManager.registerEvents(new CensureListener(censureConfig), this);
    pluginManager.registerEvents(
        new PunishmentListener(punishmentRepository, punishmentMessages), this);
    pluginManager.registerEvents(new SpinGuiListener(), this);
  }

  void setupTasks() {
    final BukkitScheduler scheduler = getServer().getScheduler();
    scheduler.runTaskTimerAsynchronously(
        this,
        new NetworkServerUpdateTask(redisMessenger, networkServerCache),
        0L, 20L
    );
    scheduler.runTaskTimerAsynchronously(this, new ActionBarTask(), 0L, 7L);
    scheduler.runTaskTimerAsynchronously(this,
        new TeleporterTask(teleporterService, messagesService), 0L, 10L);
    scheduler.runTaskTimerAsynchronously(this, new AutoMessageTask(autoMessageConfig), 0L,
        20L * autoMessageConfig.getSeconds());
  }

  void setupConfigurations() {

    this.punishmentMessages = flameConfigService.getOrCreate(getDataFolder(),
        PunishmentMessages.class);
    this.autoMessageConfig = flameConfigService.getOrCreate(getDataFolder(),
        AutoMessageConfig.class);
    this.censureConfig = flameConfigService.getOrCreate(getDataFolder(),
        CensureConfig.class);
  }

  void setupPlaceholders() {
    final Plugin placeholderAPI = getServer().getPluginManager().getPlugin("PlaceholderAPI");
    if (placeholderAPI != null) {
      new NetworkServerPlaceholder(networkServerCache, networkPlayerCache).register();
      new PlayerPlaceholder().register();
    }
  }

  void setupServices() {
    final ServicesManager servicesManager = getServer().getServicesManager();
    servicesManager.register(DatabaseConnector.class, databaseConnector, this,
        ServicePriority.Normal);
    servicesManager.register(NetworkServerCache.class, networkServerCache, this,
        ServicePriority.Normal);
    servicesManager.register(NetworkPlayerCache.class, networkPlayerCache, this,
        ServicePriority.Normal);
    servicesManager.register(RedisService.class, redisService, this, ServicePriority.Normal);
    servicesManager.register(RedisMessenger.class, redisMessenger, this,
        ServicePriority.Normal);
    servicesManager.register(BukkitMessagesService.class, messagesService, this,
        ServicePriority.Normal);
    servicesManager.register(TeleporterService.class, teleporterService, this,
        ServicePriority.Normal);
    servicesManager.register(FlameConfigService.class, flameConfigService, this,
        ServicePriority.Normal);
  }

  void setupCommands() {
    LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("commons-bukkit")
            .nativePermissions(false)
        )
        .argument(Location.class, new LocationArgument())
        .argument(Player.class, new PlayerArgument(messagesService))
        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(messagesService))
        .result(CooldownState.class, new CooldownStateResultHandlerImpl(new MessageRegistry<>()))
        .commands(LiteCommandsAnnotations.of(
            new AdminChatCommand(messagesService, networkMessageService),
            new BroadcastCommand(networkMessageService, networkServerCache),
            new HelpopCommand(messagesService, networkServerCache,
                networkPlayerCache, networkMessageService),
            new NetworkServersCommand(
                flameConfigService,
                redisMessenger,
                networkServerLoader,
                networkServerCache,
                networkServerRepository,
                networkPlayerCache
            ),
            new MessagesReloadCommand(messagesRepository),
            new AutoMessageReloadCommand(flameConfigService),
            new ExecuteCommand(redisMessenger),
            new CensureCommand(flameConfigService),
            new PunishmentCommand(redisMessenger, flameDispatcher,
                punishmentRepository, punishmentMessages, networkMessageService),
            new NetworkPlayerGhostRemoveCommand(networkPlayerCache)
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

  @Override
  public void onDisable() {
    instance = null;
    NetworkServer current = networkServerCache.getCurrent();
    NetworkServerUpdate networkServerUpdate = new NetworkServerUpdate(
        current.getName(),
        0,
        current.getStatistics().getPlayersLimit(),
        current.getStatistics().isFrozen(),
        new double[4]
    );
    redisMessenger.publish("network_servers", networkServerUpdate);

    databaseConnector.getMongoClient().close();
    redisService.getClient().close();
  }

  public RedisService getRedisService() {
    return redisService;
  }

  public RedisMessenger getRedisMessenger() {
    return redisMessenger;
  }

  public DatabaseConnector getDatabaseConnector() {
    return databaseConnector;
  }

  public NetworkMessageService getNetworkMessageService() {
    return networkMessageService;
  }

  public NetworkPlayerCache getNetworkPlayerCache() {
    return networkPlayerCache;
  }

  public NetworkServerCache getNetworkServerCache() {
    return networkServerCache;
  }

  public NetworkServerLoader getNetworkServerLoader() {
    return networkServerLoader;
  }

  public NetworkServerRepository getNetworkServerRepository() {
    return networkServerRepository;
  }

  public MessagesRepository getMessagesRepository() {
    return messagesRepository;
  }

  public BukkitMessagesService getMessagesService() {
    return messagesService;
  }

  public AutoMessageConfig getAutoMessageConfig() {
    return autoMessageConfig;
  }

  public TeleporterService getTeleporterService() {
    return teleporterService;
  }

  public CensureConfig getCensureConfig() {
    return censureConfig;
  }

  public FlameConfigService getFlameConfigService() {
    return flameConfigService;
  }

  public RemoteRepository getRemoteRepository() {
    return remoteRepository;
  }

  public PunishmentRepository getPunishmentRepository() {
    return punishmentRepository;
  }
}

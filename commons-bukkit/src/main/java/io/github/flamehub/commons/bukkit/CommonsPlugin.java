package io.github.flamehub.commons.bukkit;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.argument.ArgumentKey;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.cooldown.CooldownState;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
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
import io.github.flamehub.commons.bukkit.placeholder.PlayerPlaceholder;
import io.github.flamehub.commons.bukkit.punishment.PunishmentCommand;
import io.github.flamehub.commons.bukkit.punishment.PunishmentListener;
import io.github.flamehub.commons.bukkit.server.NetworkServerPlaceholder;
import io.github.flamehub.commons.bukkit.server.NetworkServerUpdateTask;
import io.github.flamehub.commons.bukkit.server.NetworkServersCommand;
import io.github.flamehub.commons.bukkit.teleport.TeleporterService;
import io.github.flamehub.commons.bukkit.teleport.TeleporterTask;
import io.github.flamehub.commons.bukkit.util.GsonAdapters;
import io.github.flamehub.commons.bukkit.util.JacksonAdapters;
import io.github.flamehub.commons.config.FlameConfigService;
import io.github.flamehub.commons.config.RemoteRepository;
import io.github.flamehub.commons.config.RemoteUpdateHandler;
import io.github.flamehub.commons.config.serializer.FlameGsonConfigSerializer;
import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.json.JsonUtil;
import io.github.flamehub.commons.legacy.config.MongoConfigRepository;
import io.github.flamehub.commons.legacy.config.MongoConfigService;
import io.github.flamehub.commons.message.MessagesRepository;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.network.player.NetworkPlayerHandler;
import io.github.flamehub.commons.property.PropertyLoader;
import io.github.flamehub.commons.punishment.Punishment;
import io.github.flamehub.commons.punishment.PunishmentRepository;
import io.github.flamehub.commons.redis.RedisService;
import io.github.flamehub.commons.server.*;
import io.github.flamehub.commons.util.JacksonPostHookDeserializer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

public final class CommonsPlugin extends BukkitPlugin {

    private static CommonsPlugin instance;

    public static CommonsPlugin getInstance() {
        return instance;
    }

    private RedisService redisService;
    private RedisMessenger redisMessenger;

    private DatabaseConnector databaseConnector;

    private MongoConfigRepository mongoConfigRepository;
    private MongoConfigService mongoConfigService;

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

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {

        saveResource("credentials.properties", false);
        saveResource("network.properties", false);

        final PropertyLoader networkProperties = new PropertyLoader(
                this.getDataFolder() + "/network.properties"
        );
        final String currentServerName = networkProperties.getProperty("current.server");

        final PropertyLoader credentialsProperties = new PropertyLoader(
                this.getDataFolder() + "/credentials.properties"
        );
        this.databaseConnector = new DatabaseConnector(credentialsProperties.getProperty("mongo.uri"));
        this.redisService = new RedisService(
                credentialsProperties.getProperty("redis.host"),
                credentialsProperties.getProperty("redis.password"),
                Integer.parseInt(credentialsProperties.getProperty("redis.port"))
        );
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
                this.getLogger(),
                this.networkServerCache,
                this.networkServerRepository,
                currentServerName
        );
        this.networkServerLoader.load();

        final FlameGsonConfigSerializer flameGsonConfigSerializer = new FlameGsonConfigSerializer(
                new GsonBuilder()
                        .setLongSerializationPolicy(LongSerializationPolicy.DEFAULT)
                        .serializeNulls()
                        .setPrettyPrinting()
                        .registerTypeAdapter(ItemStack.class, new GsonAdapters.ItemStackSerializer())
                        .registerTypeAdapter(ItemStack.class, new GsonAdapters.ItemStackDeserializer())
                        .registerTypeAdapter(Location.class, new GsonAdapters.LocationSerializer())
                        .registerTypeAdapter(Location.class, new GsonAdapters.LocationDeserializer())
                        .registerTypeAdapter(PotionEffect.class, new GsonAdapters.PotionEffectSerializer())
                        .registerTypeAdapter(PotionEffect.class, new GsonAdapters.PotionEffectDeserializer())
                        .registerTypeAdapter(Duration.class, new GsonAdapters.DurationSerializer())
                        .registerTypeAdapter(Duration.class, new GsonAdapters.DurationDeserializer())
                        .create()
        );
        this.remoteRepository = new RemoteRepository(
                flameGsonConfigSerializer,
                this.databaseConnector.getMongoClient(),
                this.networkServerCache.getCurrent().getCategory()
        );
        this.flameConfigService = new FlameConfigService(this.redisMessenger, this.remoteRepository, flameGsonConfigSerializer);
        this.redisMessenger.subscribe(FlameConfigService.REMOTE_CONFIG_UPDATE_CHANNEL, new RemoteUpdateHandler(this.flameConfigService));
        setupConfigurations();

        this.networkPlayerCache = new NetworkPlayerCache(this.redisService, this.redisMessenger);
        this.networkPlayerCache.load();

        final ObjectMapper mapper = JsonMapper.builder()
                .enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
                .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
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
        simpleModule.addDeserializer(PotionEffect.class, new JacksonAdapters.PotionEffectDeserializer());
        simpleModule.addSerializer(Duration.class, new JacksonAdapters.DurationSerializer());
        simpleModule.addDeserializer(Duration.class, new JacksonAdapters.DurationDeserializer());
        mapper.registerModule(simpleModule);
        mapper.registerModule(JacksonPostHookDeserializer.getSimpleModule());

        this.networkMessageService = new NetworkMessageService(this.redisMessenger, "network_messages");

        this.mongoConfigRepository = new MongoConfigRepository(
                this.databaseConnector.getMongoClient(),
                mapper,
                this.networkServerCache.getCurrent().getCategory(),
                "configs"
        );
        this.mongoConfigService = new MongoConfigService(this.mongoConfigRepository);

        this.messagesService = new BukkitMessagesService();
        this.messagesRepository = new MessagesRepository(this.databaseConnector, this.messagesService);
        this.messagesRepository.loadMessages();

        this.redisMessenger.subscribe("network_servers", new NetworkServerUpdateHandler(this.getLogger(), this.networkServerCache));
        this.redisMessenger.subscribe("network_messages", new NetworkMessageHandler(this.networkServerCache));
        this.redisMessenger.subscribe("network_players", new NetworkPlayerHandler(this.networkPlayerCache));
        this.redisMessenger.subscribe(this.networkServerCache.getCurrent().getCategory(), new ExecuteHandler(this.flameDispatcher));

        this.teleporterService = new TeleporterService();
        this.punishmentRepository = new PunishmentRepository(DatastoreFactory.create(this.databaseConnector.getMongoClient(), "global", Punishment.class));

        setupServices();
        setupCommands();
        setupPlaceholders();
        setupTasks();
        setupListeners();

    }

    void setupListeners() {
        final PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoinQuitListener(), this);
        pluginManager.registerEvents(new ProtectorListener(this.messagesService), this);
        pluginManager.registerEvents(new CensureListener(this.censureConfig), this);
        pluginManager.registerEvents(new PunishmentListener(this.punishmentRepository, this.messagesService), this);
    }

    void setupTasks() {
        final BukkitScheduler scheduler = this.getServer().getScheduler();
        scheduler.runTaskTimerAsynchronously(
                this,
                new NetworkServerUpdateTask(this.redisMessenger, this.networkServerCache),
                0L, 20L
        );
        scheduler.runTaskTimerAsynchronously(this, new TeleporterTask(this.teleporterService, this.messagesService), 0L, 10L);
        scheduler.runTaskTimerAsynchronously(this, new AutoMessageTask(this.autoMessageConfig), 0L, 20L * this.autoMessageConfig.getSeconds());
    }

    void setupConfigurations() {

        this.autoMessageConfig = this.flameConfigService.getOrCreate(this.getDataFolder(), AutoMessageConfig.class);
        this.censureConfig = this.flameConfigService.getOrCreate(this.getDataFolder(), CensureConfig.class);
        this.flameConfigService.getOrCreate(this.getDataFolder(), TestConfig.class);
    }

    void setupPlaceholders() {
        final Plugin placeholderAPI = this.getServer().getPluginManager().getPlugin("PlaceholderAPI");
        if (placeholderAPI != null) {
            new NetworkServerPlaceholder(this.networkServerCache).register();
            new PlayerPlaceholder().register();
        }
    }

    void setupServices() {
        final ServicesManager servicesManager = this.getServer().getServicesManager();
        servicesManager.register(DatabaseConnector.class, this.databaseConnector, this, ServicePriority.Normal);
        servicesManager.register(NetworkServerCache.class, this.networkServerCache, this, ServicePriority.Normal);
        servicesManager.register(NetworkPlayerCache.class, this.networkPlayerCache, this, ServicePriority.Normal);
        servicesManager.register(RedisService.class, this.redisService, this, ServicePriority.Normal);
        servicesManager.register(RedisMessenger.class, this.redisMessenger, this, ServicePriority.Normal);
        servicesManager.register(BukkitMessagesService.class, this.messagesService, this, ServicePriority.Normal);
        servicesManager.register(TeleporterService.class, this.teleporterService, this, ServicePriority.Normal);
        servicesManager.register(MongoConfigService.class, this.mongoConfigService, this, ServicePriority.Normal);
        servicesManager.register(FlameConfigService.class, this.flameConfigService, this, ServicePriority.Normal);
    }

    void setupCommands() {
        LiteBukkitFactory.builder()
                .settings(settings -> settings
                        .fallbackPrefix("commons-bukkit")
                        .nativePermissions(false)
                )
                .argument(Location.class, new LocationArgument())
                .argument(Player.class, new PlayerArgument(this.messagesService))
                .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

                .missingPermission(new MissingPermissionHandlerImpl(this.messagesService))
                .invalidUsage(new InvalidUsageHandlerImpl(this.messagesService))
                .result(CooldownState.class, new CooldownStateResultHandlerImpl(new MessageRegistry<>()))
                .commands(LiteCommandsAnnotations.of(
                        new AdminChatCommand(this.messagesService, this.networkMessageService),
                        new BroadcastCommand(this.networkMessageService, this.networkServerCache),
                        new HelpopCommand(this.messagesService, this.networkServerCache, this.networkPlayerCache, this.networkMessageService),
                        new NetworkServersCommand(
                                this.flameConfigService,
                                this.redisMessenger,
                                this.networkServerLoader,
                                this.networkServerCache,
                                this.networkServerRepository
                        ),
                        new MessagesReloadCommand(this.messagesRepository),
                        new AutoMessageReloadCommand(this.flameConfigService),
                        new ExecuteCommand(this.redisMessenger),
                        new CensureCommand(this.flameConfigService),
                        new PunishmentCommand(this.redisMessenger, this.flameDispatcher, this.punishmentRepository, this.messagesService, this.networkMessageService)
                ))
                .argumentSuggester(String.class, ArgumentKey.of("networkPlayer"), (invocation, argument, context) -> this.networkPlayerCache.values()
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
        NetworkServer current = this.networkServerCache.getCurrent();
        NetworkServerUpdate networkServerUpdate = new NetworkServerUpdate(
                current.getName(),
                0,
                current.getStatistics().getPlayersLimit(),
                current.getStatistics().isFrozen(),
                new double[4]
        );
        this.redisMessenger.publish("network_servers", networkServerUpdate);

        this.databaseConnector.getMongoClient().close();
        this.redisService.getClient().close();
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

    public MongoConfigRepository getMongoConfigRepository() {
        return mongoConfigRepository;
    }

    public MongoConfigService getMongoConfigService() {
        return mongoConfigService;
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

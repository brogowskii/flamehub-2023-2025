package io.github.flamehub.lobby;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import io.github.flamehub.commons.bukkit.BukkitPlugin;
import io.github.flamehub.commons.bukkit.command.argument.LocationArgument;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.argument.WorldArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.sidebar.SidebarCache;
import io.github.flamehub.commons.bukkit.sidebar.SidebarListener;
import io.github.flamehub.commons.bukkit.sidebar.SidebarUpdaterTask;
import io.github.flamehub.commons.bukkit.tab.DefaultTablistProvider;
import io.github.flamehub.commons.bukkit.tab.TablistService;
import io.github.flamehub.commons.bukkit.tab.TablistTask;
import io.github.flamehub.commons.bukkit.user.UserDatabaseListener;
import io.github.flamehub.commons.config.MongoConfigService;
import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.lobby.command.JoinServerCommand;
import io.github.flamehub.lobby.daily.*;
import io.github.flamehub.lobby.listener.TabCompleteListener;
import io.github.flamehub.lobby.selector.ServerSelectorCommand;
import io.github.flamehub.lobby.selector.ServerSelectorConfig;
import io.github.flamehub.lobby.selector.ServerSelectorListener;
import io.github.flamehub.lobby.sidebar.SidebarUpdaterImpl;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

public final class LobbyPlugin extends BukkitPlugin {

    private DatabaseConnector databaseConnector;
    private MongoConfigService mongoConfigService;
    private RedisMessenger redisMessenger;
    private NetworkServerCache networkServerCache;
    private ServerSelectorConfig serverSelectorConfig;

    private DailyUserFactory dailyUserFactory;
    private DailyUserCache dailyUserCache;
    private DailyUserRepository dailyUserRepository;

    private BukkitMessagesService messagesService;
    private SidebarCache sidebarCache;

    private TablistService tablistService;

    @Override
    public void onEnable() {
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        this.redisMessenger = getService(RedisMessenger.class);
        this.databaseConnector = getService(DatabaseConnector.class);
        this.mongoConfigService = getService(MongoConfigService.class);
        this.networkServerCache = getService(NetworkServerCache.class);
        this.messagesService = getService(BukkitMessagesService.class);

        this.serverSelectorConfig = this.mongoConfigService.findOrCreate(
                ServerSelectorConfig.class,
                "server_selectors",
                ServerSelectorConfig::new
        );

        this.sidebarCache = new SidebarCache();
        this.tablistService = new TablistService(new DefaultTablistProvider(this.messagesService));

        this.dailyUserFactory = new DailyUserFactory();
        this.dailyUserRepository = new DailyUserRepository(DatastoreFactory.create(this.databaseConnector.getMongoClient(), "lobby", DailyUser.class));
        this.dailyUserCache = new DailyUserCache(this.dailyUserRepository);

        setupTasks();
        setupListeners();
        setupCommands();

    }

    void setupTasks() {
        BukkitScheduler scheduler = this.getServer().getScheduler();
        scheduler.runTaskTimerAsynchronously(this, new SidebarUpdaterTask(this.sidebarCache, new SidebarUpdaterImpl(this.messagesService)), 0, 40L);
        scheduler.runTaskTimerAsynchronously(this, new TablistTask(this.tablistService), 0L, 20L);
    }

    void setupListeners() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(
                new ServerSelectorListener(
                        this,
                        this.redisMessenger, this.serverSelectorConfig,

                        this.networkServerCache,
                        this.messagesService), this
        );
        pluginManager.registerEvents(new DailyListener(this.flameDispatcher, this.dailyUserCache, this.dailyUserRepository), this);
        pluginManager.registerEvents(new UserDatabaseListener<>(this.flameDispatcher, pluginManager, this.dailyUserCache, this.dailyUserRepository, this.dailyUserFactory), this);
        pluginManager.registerEvents(new SidebarListener(this.sidebarCache), this);
        pluginManager.registerEvents(new TabCompleteListener(), this);
    }

    void setupCommands() {
        LiteBukkitFactory.builder()
                .settings(settings -> settings
                        .fallbackPrefix("flamehub-lobby")
                        .nativePermissions(false)
                )
                .argument(Location.class, new LocationArgument())
                .argument(World.class, new WorldArgument())
                .argument(Player.class, new PlayerArgument(this.messagesService))

                .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

                .missingPermission(new MissingPermissionHandlerImpl(this.messagesService))
                .invalidUsage(new InvalidUsageHandlerImpl(this.messagesService))

                .commands(LiteCommandsAnnotations.of(
                        new ServerSelectorCommand(this, this.serverSelectorConfig, this.mongoConfigService),
                        new JoinServerCommand(this, this.redisMessenger, this.messagesService, this.networkServerCache)
                ))
                .schematicGenerator(SchematicFormat.angleBrackets())
                .build();
    }
}
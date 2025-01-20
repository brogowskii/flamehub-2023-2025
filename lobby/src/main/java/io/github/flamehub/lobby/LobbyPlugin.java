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
import io.github.flamehub.commons.config.FlameConfigService;
import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.lobby.command.JoinServerCommand;
import io.github.flamehub.lobby.daily.DailyListener;
import io.github.flamehub.lobby.daily.DailyUser;
import io.github.flamehub.lobby.daily.DailyUserCache;
import io.github.flamehub.lobby.daily.DailyUserFactory;
import io.github.flamehub.lobby.daily.DailyUserRepository;
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
  private FlameConfigService flameConfigService;
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
    getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

    this.redisMessenger = getService(RedisMessenger.class);
    this.databaseConnector = getService(DatabaseConnector.class);
    this.flameConfigService = getService(FlameConfigService.class);
    this.networkServerCache = getService(NetworkServerCache.class);
    this.messagesService = getService(BukkitMessagesService.class);

    this.serverSelectorConfig = flameConfigService.getOrCreate(
        getDataFolder(),
        ServerSelectorConfig.class
    );

    this.sidebarCache = new SidebarCache();
    this.tablistService = new TablistService(new DefaultTablistProvider(messagesService));

    this.dailyUserFactory = new DailyUserFactory();
    this.dailyUserRepository = new DailyUserRepository(
        DatastoreFactory.create(databaseConnector.getMongoClient(), "lobby", DailyUser.class));
    this.dailyUserCache = new DailyUserCache(dailyUserRepository);

    setupTasks();
    setupListeners();
    setupCommands();

  }

  void setupTasks() {
    BukkitScheduler scheduler = getServer().getScheduler();
    scheduler.runTaskTimerAsynchronously(this,
        new SidebarUpdaterTask(sidebarCache, new SidebarUpdaterImpl(messagesService)), 0,
        40L);
    scheduler.runTaskTimerAsynchronously(this, new TablistTask(tablistService), 0L, 20L);
  }

  void setupListeners() {
    PluginManager pluginManager = getServer().getPluginManager();
    pluginManager.registerEvents(
        new ServerSelectorListener(
            this,
            redisMessenger, serverSelectorConfig,

            networkServerCache,
            messagesService), this
    );
    pluginManager.registerEvents(
        new DailyListener(flameDispatcher, dailyUserCache, dailyUserRepository),
        this);
    pluginManager.registerEvents(
        new UserDatabaseListener<>(flameDispatcher, pluginManager, dailyUserCache,
            dailyUserRepository, dailyUserFactory), this);
    pluginManager.registerEvents(new SidebarListener(sidebarCache), this);
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
        .argument(Player.class, new PlayerArgument(messagesService))

        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(messagesService))

        .commands(LiteCommandsAnnotations.of(
            new ServerSelectorCommand(flameConfigService),
            new JoinServerCommand(this, redisMessenger, messagesService,
                networkServerCache)
        ))
        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();
  }
}
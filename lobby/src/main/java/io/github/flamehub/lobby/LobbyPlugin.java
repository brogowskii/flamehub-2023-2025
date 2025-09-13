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
import io.github.flamehub.commons.bukkit.nametag.NameTagListener;
import io.github.flamehub.commons.bukkit.nametag.NameTagService;
import io.github.flamehub.commons.bukkit.sidebar.SidebarCache;
import io.github.flamehub.commons.bukkit.sidebar.SidebarListener;
import io.github.flamehub.commons.bukkit.sidebar.SidebarUpdaterTask;
import io.github.flamehub.commons.bukkit.tab.HeaderFooterTablistService;
import io.github.flamehub.commons.bukkit.tab.TablistTask;
import io.github.flamehub.commons.bukkit.user.UserDatabaseListener;
import io.github.flamehub.commons.config.FlameConfigService;
import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.server.NetworkServerFacade;
import io.github.flamehub.lobby.command.JoinServerCommand;
import io.github.flamehub.lobby.daily.DailyListener;
import io.github.flamehub.lobby.daily.DailyUser;
import io.github.flamehub.lobby.daily.DailyUserCache;
import io.github.flamehub.lobby.daily.DailyUserFactory;
import io.github.flamehub.lobby.daily.DailyUserRepository;
import io.github.flamehub.lobby.listener.TabCompleteListener;
import io.github.flamehub.lobby.nametag.NameTagProviderImpl;
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
  private NetworkServerFacade networkServerFacade;
  private ServerSelectorConfig serverSelectorConfig;

  private DailyUserFactory dailyUserFactory;
  private DailyUserCache dailyUserCache;
  private DailyUserRepository dailyUserRepository;

  private BukkitMessagesService messagesService;
  private SidebarCache sidebarCache;

  private NameTagService nameTagService;
  private NameTagProviderImpl nameTagProvider;


  @Override
  public void onEnable() {
    getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

    redisMessenger = getService(RedisMessenger.class);
    databaseConnector = getService(DatabaseConnector.class);
    flameConfigService = getService(FlameConfigService.class);
    networkServerFacade = getService(NetworkServerFacade.class);
    messagesService = getService(BukkitMessagesService.class);

    serverSelectorConfig = flameConfigService.getOrCreate(ServerSelectorConfig.class);

    sidebarCache = new SidebarCache();

    dailyUserFactory = new DailyUserFactory();
    dailyUserRepository = new DailyUserRepository(
        DatastoreFactory.create(databaseConnector.getMongoClient(), "lobby", DailyUser.class));
    dailyUserCache = new DailyUserCache(dailyUserRepository);

    nameTagProvider = new NameTagProviderImpl();
    nameTagService = new NameTagService(flameDispatcher, nameTagProvider);

    setupTasks();
    setupListeners();
    setupCommands();

    new LobbyPlaceholder().register();

  }

  void setupTasks() {
    final BukkitScheduler scheduler = getServer().getScheduler();
    scheduler.runTaskTimerAsynchronously(this,
        new SidebarUpdaterTask(sidebarCache, new SidebarUpdaterImpl(messagesService)), 0,
        40L);
  }

  void setupListeners() {
    final PluginManager pluginManager = getServer().getPluginManager();
    pluginManager.registerEvents(
        new ServerSelectorListener(
            this,
            redisMessenger, serverSelectorConfig,

            networkServerFacade,
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
    pluginManager.registerEvents(new NameTagListener(nameTagService, flameDispatcher), this);
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
                networkServerFacade)
        ))
        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();
  }
}
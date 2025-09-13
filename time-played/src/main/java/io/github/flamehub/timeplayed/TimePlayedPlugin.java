package io.github.flamehub.timeplayed;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.command.argument.LocationArgument;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.bukkit.user.UserDatabaseListener;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.timeplayed.shop.TimePlayedShopAdminCommand;
import io.github.flamehub.timeplayed.shop.TimePlayedShopCommand;
import io.github.flamehub.timeplayed.shop.TimePlayedShopConfig;
import io.github.flamehub.timeplayed.user.TimePlayedUser;
import io.github.flamehub.timeplayed.user.TimePlayedUserCache;
import io.github.flamehub.timeplayed.user.TimePlayedUserFactory;
import io.github.flamehub.timeplayed.user.TimePlayedUserIncrementTask;
import io.github.flamehub.timeplayed.user.TimePlayedUserListener;
import io.github.flamehub.timeplayed.user.TimePlayedUserRepository;
import io.github.flamehub.timeplayed.user.TimePlayedUserSaver;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.scheduler.BukkitScheduler;

public final class TimePlayedPlugin extends BukkitModule {

  private TimePlayedShopConfig timePlayedShopConfig;
  private TimePlayedUserRepository timePlayedUserRepository;
  private TimePlayedUserFactory timePlayedUserFactory;
  private TimePlayedUserCache timePlayedUserCache;
  private TimePlayedUserSaver timePlayedUserSaver;

  @Override
  public void onEnable() {
    super.onEnable();

    timePlayedShopConfig = flameConfigService.getOrCreate(
        TimePlayedShopConfig.class);
    timePlayedUserRepository = new TimePlayedUserRepository(
        DatastoreFactory.create(databaseConnector.getMongoClient(),
            networkServerFacade.getCurrent().getCategory(), TimePlayedUser.class),
        TimePlayedUser.class
    );
    timePlayedUserFactory = new TimePlayedUserFactory();
    timePlayedUserCache = new TimePlayedUserCache(timePlayedUserRepository);
    timePlayedUserSaver = new TimePlayedUserSaver(timePlayedUserRepository,
        timePlayedUserCache);

    getServer().getServicesManager()
        .register(TimePlayedUserCache.class, timePlayedUserCache, this,
            ServicePriority.Normal);

    setupPlaceholders();
    setupTasks();
    setupListeners();
    setupCommands();

  }

  @Override
  public void onDisable() {
    timePlayedUserSaver.run();
  }

  void setupPlaceholders() {
    new TimePlayedPlaceholder(timePlayedUserCache).register();
  }

  void setupTasks() {
    final BukkitScheduler scheduler = getServer().getScheduler();
    scheduler.runTaskTimerAsynchronously(this, timePlayedUserSaver, 0L, 20 * 150L);
    scheduler.runTaskTimerAsynchronously(this,
        new TimePlayedUserIncrementTask(this, timePlayedUserCache), 0L, 20 * 15L);
  }

  void setupListeners() {
    final PluginManager pluginManager = getServer().getPluginManager();
    final UserDatabaseListener<TimePlayedUser> listener = new UserDatabaseListener<>(
        flameDispatcher,
        pluginManager,
        timePlayedUserCache,
        timePlayedUserRepository,
        timePlayedUserFactory
    );
    pluginManager.registerEvents(listener, this);
    pluginManager.registerEvents(new TimePlayedUserListener(timePlayedUserCache), this);
  }

  void setupCommands() {
    LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("time-played")
            .nativePermissions(false)
        )
        .argument(Location.class, new LocationArgument())
        .argument(Player.class, new PlayerArgument(messagesService))
        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(messagesService))

        .commands(LiteCommandsAnnotations.of(
            new TimePlayedShopCommand(timePlayedShopConfig, timePlayedUserCache),
            new TimePlayedShopAdminCommand(flameConfigService, timePlayedShopConfig)
        ))

        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();
  }

}

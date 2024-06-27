package io.github.flamehub.timeplayed;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import io.github.flamehub.commons.bukkit.BukkitPlugin;
import io.github.flamehub.commons.legacy.config.MongoConfigService;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.server.NetworkServerCache;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.scheduler.BukkitScheduler;
import io.github.flamehub.commons.bukkit.command.argument.LocationArgument;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.user.UserDatabaseListener;
import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.timeplayed.shop.TimePlayedShopAdminCommand;
import io.github.flamehub.timeplayed.shop.TimePlayedShopCommand;
import io.github.flamehub.timeplayed.shop.TimePlayedShopConfig;
import io.github.flamehub.timeplayed.user.*;

public final class TimePlayedPlugin extends BukkitPlugin {

    private DatabaseConnector databaseConnector;
    private NetworkServerCache networkServerCache;
    private BukkitMessagesService messagesService;
    private MongoConfigService mongoConfigService;

    private TimePlayedShopConfig timePlayedShopConfig;
    private TimePlayedUserRepository timePlayedUserRepository;
    private TimePlayedUserFactory timePlayedUserFactory;
    private TimePlayedUserCache timePlayedUserCache;
    private TimePlayedUserSaver timePlayedUserSaver;

    @Override
    public void onEnable() {

        this.networkServerCache = getService(NetworkServerCache.class);
        this.databaseConnector = getService(DatabaseConnector.class);
        this.messagesService = getService(BukkitMessagesService.class);
        this.mongoConfigService = getService(MongoConfigService.class);

        this.timePlayedShopConfig = this.mongoConfigService.findOrCreate(TimePlayedShopConfig.class, "time_played_shop", TimePlayedShopConfig::new);
        this.timePlayedUserRepository = new TimePlayedUserRepository(
                DatastoreFactory.create(this.databaseConnector.getMongoClient(), this.networkServerCache.getCurrent().getCategory(), TimePlayedUser.class), TimePlayedUser.class
        );
        this.timePlayedUserFactory = new TimePlayedUserFactory();
        this.timePlayedUserCache = new TimePlayedUserCache(this.timePlayedUserRepository);
        this.timePlayedUserSaver = new TimePlayedUserSaver(this.timePlayedUserRepository, this.timePlayedUserCache);

        this.getServer().getServicesManager().register(TimePlayedUserCache.class, this.timePlayedUserCache, this, ServicePriority.Normal);

        setupPlaceholders();
        setupTasks();
        setupListeners();
        setupCommands();

    }

    @Override
    public void onDisable() {
        this.timePlayedUserSaver.run();
    }

    void setupPlaceholders() {
        new TimePlayedPlaceholder(this.timePlayedUserCache).register();
    }

    void setupTasks() {
        BukkitScheduler scheduler = this.getServer().getScheduler();
        scheduler.runTaskTimerAsynchronously(this, this.timePlayedUserSaver, 0L, 20 * 150L);
        scheduler.runTaskTimerAsynchronously(this, new TimePlayedUserIncrementTask(this, this.timePlayedUserCache), 0L, 20 * 15L);
    }

    void setupListeners() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        UserDatabaseListener<TimePlayedUser> listener = new UserDatabaseListener<>(
                this.flameDispatcher,
                pluginManager,
                this.timePlayedUserCache,
                this.timePlayedUserRepository,
                this.timePlayedUserFactory
        );
        pluginManager.registerEvents(listener, this);
        pluginManager.registerEvents(new TimePlayedUserListener(this.timePlayedUserCache), this);
    }

    void setupCommands() {
        LiteBukkitFactory.builder()
                .settings(settings -> settings
                        .fallbackPrefix("time-played")
                        .nativePermissions(false)
                )
                .argument(Location.class, new LocationArgument())
                .argument(Player.class, new PlayerArgument(this.messagesService))
                .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

                .missingPermission(new MissingPermissionHandlerImpl(this.messagesService))
                .invalidUsage(new InvalidUsageHandlerImpl(this.messagesService))

                .commands(LiteCommandsAnnotations.of(
                        new TimePlayedShopCommand(this.timePlayedShopConfig, this.timePlayedUserCache),
                        new TimePlayedShopAdminCommand(this.mongoConfigService, this.timePlayedShopConfig)
                ))

                .schematicGenerator(SchematicFormat.angleBrackets())
                .build();
    }

}

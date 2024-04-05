package io.github.flamehub.achievements;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import io.github.flamehub.achievements.achievement.AchievementAdminCommand;
import io.github.flamehub.achievements.achievement.AchievementCommand;
import io.github.flamehub.achievements.achievement.AchievementConfig;
import io.github.flamehub.achievements.achievement.AchievementService;
import io.github.flamehub.achievements.achievement.user.*;
import io.github.flamehub.achievements.achievement.*;
import io.github.flamehub.commons.bukkit.BukkitPlugin;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.user.UserDatabaseListener;
import io.github.flamehub.commons.config.MongoConfigService;
import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.server.NetworkServerCache;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

public final class AchievementsPlugin extends BukkitPlugin {

    private BukkitMessagesService messagesService;

    private NetworkServerCache networkServerCache;
    private NetworkMessageService networkMessageService;
    private RedisMessenger redisMessenger;
    private DatabaseConnector databaseConnector;
    private MongoConfigService mongoConfigService;

    private AchievementConfig achievementConfig;
    private AchievementService achievementService;

    private AchievementUserRepository achievementUserRepository;
    private AchievementUserFactory achievementUserFactory;
    private AchievementUserCache achievementUserCache;

    @Override
    public void onEnable() {

        this.messagesService = getService(BukkitMessagesService.class);
        this.networkServerCache = getService(NetworkServerCache.class);
        this.redisMessenger = getService(RedisMessenger.class);
        this.databaseConnector = getService(DatabaseConnector.class);
        this.mongoConfigService = getService(MongoConfigService.class);
        this.networkMessageService = new NetworkMessageService(this.redisMessenger, "network_messages");

        this.achievementConfig = this.mongoConfigService.findOrCreate(AchievementConfig.class, "achievements", AchievementConfig::new);
        this.achievementService = new AchievementService(this.achievementConfig);

        this.achievementUserRepository = new AchievementUserRepository(
                DatastoreFactory.create(this.databaseConnector.getMongoClient(), this.networkServerCache.getCurrent().getCategory(), AchievementUser.class)
        );
        this.achievementUserFactory = new AchievementUserFactory(AchievementUser::new);
        this.achievementUserCache = new AchievementUserCache(this.achievementUserRepository);

        BukkitScheduler scheduler = this.getServer().getScheduler();
        scheduler.runTaskTimerAsynchronously(this, new AchievementUserSaver(this.achievementUserRepository, this.achievementUserCache), 0L, 20 * 60L);

        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new AchievementListener(this.achievementService, this.achievementUserCache), this);
        pluginManager.registerEvents(new UserDatabaseListener<>(this.flameDispatcher, pluginManager, this.achievementUserCache, this.achievementUserRepository, this.achievementUserFactory), this);

        LiteBukkitFactory.builder()
                .settings(settings -> settings
                        .fallbackPrefix("achievements")
                        .nativePermissions(false)
                )
                .argument(Player.class, new PlayerArgument(this.messagesService))
                .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

                .missingPermission(new MissingPermissionHandlerImpl(this.messagesService))
                .invalidUsage(new InvalidUsageHandlerImpl(this.messagesService))

                .commands(LiteCommandsAnnotations.of(
                        new AchievementCommand(this.achievementConfig, this.achievementService, this.achievementUserCache, this.achievementUserRepository, networkMessageService, networkServerCache),
                        new AchievementAdminCommand(this.mongoConfigService, this.achievementConfig, this.achievementService)
                ))

                .schematicGenerator(SchematicFormat.angleBrackets())
                .build();


    }

    public BukkitMessagesService getMessagesService() {
        return messagesService;
    }

    public NetworkServerCache getNetworkServerCache() {
        return networkServerCache;
    }

    public NetworkMessageService getNetworkMessageService() {
        return networkMessageService;
    }

    public RedisMessenger getRedisMessenger() {
        return redisMessenger;
    }

    public DatabaseConnector getDatabaseConnector() {
        return databaseConnector;
    }

    public MongoConfigService getMongoConfigService() {
        return mongoConfigService;
    }

    public AchievementConfig getAchievementConfig() {
        return achievementConfig;
    }

    public AchievementService getAchievementService() {
        return achievementService;
    }

    public AchievementUserRepository getAchievementUserRepository() {
        return achievementUserRepository;
    }

    public AchievementUserFactory getAchievementUserFactory() {
        return achievementUserFactory;
    }

    public AchievementUserCache getAchievementUserCache() {
        return achievementUserCache;
    }
}
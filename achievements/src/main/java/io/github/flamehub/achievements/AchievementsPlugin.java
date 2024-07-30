package io.github.flamehub.achievements;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import io.github.flamehub.achievements.achievement.AchievementAdminCommand;
import io.github.flamehub.achievements.achievement.AchievementCommand;
import io.github.flamehub.achievements.achievement.AchievementConfig;
import io.github.flamehub.achievements.achievement.AchievementListener;
import io.github.flamehub.achievements.achievement.AchievementService;
import io.github.flamehub.achievements.achievement.user.AchievementUser;
import io.github.flamehub.achievements.achievement.user.AchievementUserCache;
import io.github.flamehub.achievements.achievement.user.AchievementUserFactory;
import io.github.flamehub.achievements.achievement.user.AchievementUserRepository;
import io.github.flamehub.achievements.achievement.user.AchievementUserSaver;
import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.bukkit.user.UserDatabaseListener;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

public final class AchievementsPlugin extends BukkitModule {

  private NetworkMessageService networkMessageService;

  private AchievementConfig achievementConfig;
  private AchievementService achievementService;

  private AchievementUserRepository achievementUserRepository;
  private AchievementUserFactory achievementUserFactory;
  private AchievementUserCache achievementUserCache;

  @Override
  public void onEnable() {
    super.onEnable();

    this.networkMessageService = new NetworkMessageService(this.redisMessenger, "network_messages");

    this.achievementConfig = this.flameConfigService.getOrCreate(this.getDataFolder(),
        AchievementConfig.class);
    this.achievementService = new AchievementService(this.achievementConfig);

    this.achievementUserRepository = new AchievementUserRepository(
        DatastoreFactory.create(this.databaseConnector.getMongoClient(),
            this.networkServerCache.getCurrent().getCategory(), AchievementUser.class)
    );
    this.achievementUserFactory = new AchievementUserFactory(AchievementUser::new);
    this.achievementUserCache = new AchievementUserCache(this.achievementUserRepository);

    BukkitScheduler scheduler = this.getServer().getScheduler();
    scheduler.runTaskTimerAsynchronously(this,
        new AchievementUserSaver(this.achievementUserRepository, this.achievementUserCache), 0L,
        20 * 60L);

    PluginManager pluginManager = this.getServer().getPluginManager();
    pluginManager.registerEvents(
        new AchievementListener(this.achievementService, this.achievementUserCache), this);
    pluginManager.registerEvents(
        new UserDatabaseListener<>(this.flameDispatcher, pluginManager, this.achievementUserCache,
            this.achievementUserRepository, this.achievementUserFactory), this);

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
            new AchievementCommand(this.achievementConfig, this.achievementService,
                this.achievementUserCache, this.achievementUserRepository, networkMessageService,
                networkServerCache),
            new AchievementAdminCommand(this.flameConfigService)
        ))

        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();


  }

  public NetworkMessageService getNetworkMessageService() {
    return networkMessageService;
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
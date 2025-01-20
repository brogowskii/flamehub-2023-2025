package io.github.flamehub.daily.reward;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import io.github.flamehub.commons.bukkit.BukkitPlugin;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.user.UserDatabaseListener;
import io.github.flamehub.commons.config.FlameConfigService;
import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.daily.reward.user.DailyRewardUser;
import io.github.flamehub.daily.reward.user.DailyRewardUserCache;
import io.github.flamehub.daily.reward.user.DailyRewardUserFactory;
import io.github.flamehub.daily.reward.user.DailyRewardUserRepository;
import org.bukkit.entity.Player;

public final class DailyRewardPlugin extends BukkitPlugin {

  private BukkitMessagesService messagesService;

  private NetworkServerCache networkServerCache;
  private DatabaseConnector databaseConnector;
  private FlameConfigService flameConfigService;

  private DailyRewardConfig dailyRewardConfig;
  private DailyRewardUserCache dailyRewardUserCache;
  private DailyRewardUserRepository dailyRewardUserRepository;
  private DailyRewardUserFactory dailyRewardUserFactory;

  @Override
  public void onEnable() {

    this.networkServerCache = getService(NetworkServerCache.class);
    this.messagesService = getService(BukkitMessagesService.class);
    this.databaseConnector = getService(DatabaseConnector.class);
    this.flameConfigService = getService(FlameConfigService.class);

    this.dailyRewardConfig = flameConfigService.getOrCreate(getDataFolder(),
        DailyRewardConfig.class);

    this.dailyRewardUserRepository = new DailyRewardUserRepository(
        DatastoreFactory.create(
            databaseConnector.getMongoClient(),
            networkServerCache.getCurrent().getCategory(),
            DailyRewardUser.class
        )
    );
    this.dailyRewardUserCache = new DailyRewardUserCache(dailyRewardUserRepository);
    this.dailyRewardUserFactory = new DailyRewardUserFactory();

    getServer().getPluginManager().registerEvents(
        new UserDatabaseListener<>(flameDispatcher, getServer().getPluginManager(),
            dailyRewardUserCache, dailyRewardUserRepository, dailyRewardUserFactory),
        this);

    LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("flamehub-daily-rewards")
            .nativePermissions(false)
        )
        .argument(Player.class, new PlayerArgument(messagesService))
        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(messagesService))

        .commands(LiteCommandsAnnotations.of(
            new DailyRewardCommand(
                flameConfigService,
                flameDispatcher,
                dailyRewardConfig,
                dailyRewardUserCache,
                dailyRewardUserRepository
            ))
        )
        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();
  }
}

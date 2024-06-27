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
import io.github.flamehub.commons.legacy.config.MongoConfigService;
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
    private MongoConfigService mongoConfigService;

    private DailyRewardConfig dailyRewardConfig;
    private DailyRewardUserCache dailyRewardUserCache;
    private DailyRewardUserRepository dailyRewardUserRepository;
    private DailyRewardUserFactory dailyRewardUserFactory;

    @Override
    public void onEnable() {

        this.networkServerCache = getService(NetworkServerCache.class);
        this.messagesService = getService(BukkitMessagesService.class);
        this.databaseConnector = getService(DatabaseConnector.class);
        this.mongoConfigService = getService(MongoConfigService.class);

        this.dailyRewardConfig = this.mongoConfigService.findOrCreate(DailyRewardConfig.class, "daily_rewards", DailyRewardConfig::new);

        this.dailyRewardUserRepository = new DailyRewardUserRepository(
                DatastoreFactory.create(
                        this.databaseConnector.getMongoClient(),
                        this.networkServerCache.getCurrent().getCategory(),
                        DailyRewardUser.class
                )
        );
        this.dailyRewardUserCache = new DailyRewardUserCache(this.dailyRewardUserRepository);
        this.dailyRewardUserFactory = new DailyRewardUserFactory();

        this.getServer().getPluginManager().registerEvents(new UserDatabaseListener<>(this.flameDispatcher, this.getServer().getPluginManager(), this.dailyRewardUserCache, this.dailyRewardUserRepository, this.dailyRewardUserFactory), this);

        LiteBukkitFactory.builder()
                .settings(settings -> settings
                        .fallbackPrefix("daily-rewards")
                        .nativePermissions(false)
                )
                .argument(Player.class, new PlayerArgument(this.messagesService))
                .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

                .missingPermission(new MissingPermissionHandlerImpl(this.messagesService))
                .invalidUsage(new InvalidUsageHandlerImpl(this.messagesService))

                .commands(
                        LiteCommandsAnnotations.of(
                                new DailyRewardCommand(mongoConfigService, this.flameDispatcher, this.dailyRewardConfig, this.dailyRewardUserCache, this.dailyRewardUserRepository)
                        )
                )
                .schematicGenerator(SchematicFormat.angleBrackets())
                .build();
    }
}

package io.github.flamehub.reward.bot;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.json.gson.JsonGsonConfigurer;
import io.github.flamehub.commons.database.DatabaseConfig;
import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.network.player.NetworkPlayerHandler;
import io.github.flamehub.commons.property.PropertyLoader;
import io.github.flamehub.commons.redis.RedisConfig;
import io.github.flamehub.commons.redis.RedisService;
import io.github.flamehub.commons.server.*;
import io.github.flamehub.reward.api.RewardReceivedEntry;
import io.github.flamehub.reward.api.RewardReceivedEntryRepository;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.logging.Logger;

public final class RewardBot {

    private final DatabaseConnector databaseConnector;

    private final RedisService redisService;
    private final RedisMessenger redisMessenger;

    private final NetworkPlayerCache networkPlayerCache;
    private final NetworkServerCache networkServerCache;
    private final NetworkServerRepository networkServerRepository;
    private final NetworkServerLoader networkServerLoader;

    private final RewardReceivedEntryRepository rewardReceivedEntryRepository;

    private final JDA jda;

    public RewardBot() {
        final PropertyLoader credentials = new PropertyLoader("credentials.properties");
        this.databaseConnector = new DatabaseConnector(credentials.getProperty("mongo.uri"));
        this.redisService = new RedisService(
                credentials.getProperty("redis.host"),
                credentials.getProperty("redis.password"),
                Integer.parseInt(credentials.getProperty("redis.port"))
        );
        this.redisMessenger = new RedisMessenger(this.redisService.getClient());
        this.redisMessenger.subscribeCallbacks("callbacks");

        this.networkPlayerCache = new NetworkPlayerCache(this.redisService, this.redisMessenger);
        this.networkPlayerCache.load();
        this.redisMessenger.subscribe("network_players", new NetworkPlayerHandler(this.networkPlayerCache));

        this.rewardReceivedEntryRepository = new RewardReceivedEntryRepository(
                DatastoreFactory.create(
                        this.databaseConnector.getMongoClient(),
                        "global",
                        RewardReceivedEntry.class
                ),
                RewardReceivedEntry.class
        );

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
                Logger.getLogger(RewardBot.class.getSimpleName()),
                this.networkServerCache,
                this.networkServerRepository,
                "reward-bot"
        );
        this.networkServerLoader.load();

        this.redisMessenger.subscribe("network_servers", new NetworkServerUpdateHandler(Logger.getLogger("RewardBot"), this.networkServerCache));

        this.jda = JDABuilder.createLight("MTA3NTUwMTQyODQ3NjQ3NzU2Mw.GqvTJy.u_f935QnZxqCbTk7LoqdbfVNtzYNp4SwZESySk")
                .enableIntents(
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                        GatewayIntent.GUILD_WEBHOOKS,
                        GatewayIntent.GUILD_INVITES,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.GUILD_MESSAGE_TYPING,
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                        GatewayIntent.DIRECT_MESSAGE_TYPING,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.SCHEDULED_EVENTS
                )
                .addEventListeners(
                        new RewardBotListeners(this.rewardReceivedEntryRepository, this.networkPlayerCache, this.networkServerCache, this.redisMessenger)
                )
                .setActivity(Activity.of(Activity.ActivityType.STREAMING, "Flamehub.pl - Reward System"))
                .build();
    }

}

package io.github.flamehub.economy.user;

import com.mongodb.client.MongoClient;
import dev.morphia.Morphia;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.bukkit.LiteBukkitSettings;
import io.github.flamehub.commons.bukkit.BukkitConfigurator;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.server.NetworkServerCache;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class EconomyUserConfigurator extends BukkitConfigurator {

    public EconomyUserFacade economyUserFacade(
            final LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> liteCommandsBuilder,
            final BukkitMessagesService messagesService,
            final Plugin plugin,
            final FlameDispatcher flameDispatcher,
            final RedisMessenger redisMessenger,
            final NetworkServerCache networkServerCache,
            final NetworkPlayerCache networkPlayerCache,
            final MongoClient mongoClient,
            final String databaseName,
            final String currentServerName
    ) {

        final EconomyUserRepository economyUserRepository = new EconomyUserRepository(Morphia.createDatastore(mongoClient, databaseName));
        final EconomyUserFactory economyUserFactory = new EconomyUserFactory();
        final EconomyUserCache economyUserCache = new EconomyUserCache(economyUserRepository);

        final EconomyUserSaver economyUserSaver = new EconomyUserSaver(
                economyUserRepository,
                economyUserCache
        );

        final EconomyUserUpdater economyUserUpdater = new EconomyUserUpdater(
                networkServerCache,
                networkPlayerCache,
                economyUserRepository,
                redisMessenger
        );

        final EconomyUserFacade economyUserFacade = new EconomyUserFacade(
                economyUserCache,
                economyUserRepository,
                economyUserUpdater,
                economyUserSaver
        );


        final BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.runTaskTimerAsynchronously(plugin, economyUserSaver, 0L, 20 * 60L);

        redisMessenger.subscribe(currentServerName, new EconomyUserUpdateHandler(economyUserFacade));

        super.registerListeners(
                plugin,
                new EconomyUserListener(
                        flameDispatcher,
                        plugin.getServer().getPluginManager(),
                        economyUserCache,
                        economyUserRepository,
                        economyUserFactory
                )
        );

        liteCommandsBuilder
                .argument(EconomyUser.class, new EconomyUserArgument(economyUserCache, messagesService))
                .context(EconomyUser.class, new EconomyUserContextual(economyUserCache));

        return economyUserFacade;
    }

}

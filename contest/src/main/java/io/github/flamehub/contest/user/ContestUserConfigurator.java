package io.github.flamehub.contest.user;

import dev.morphia.Datastore;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerFacade;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

public final class ContestUserConfigurator {

  public ContestUserConfigurator() {
  }

  public static ContestUserFacade create(
      final Plugin plugin,
      final FlameDispatcher flameDispatcher,
      final NetworkServerFacade networkServerFacade,
      final NetworkPlayerCache networkPlayerCache,
      final RedisMessenger redisMessenger,
      final Datastore datastore
  ) {

    final ContestUserRepository contestUserRepository = new ContestUserRepository(datastore);
    final ContestUserCache contestUserCache = new ContestUserCache(contestUserRepository);
    final ContestUserFactory contestUserFactory = new ContestUserFactory();
    final ContestUserSaver contestUserSaver = new ContestUserSaver(contestUserRepository,
        contestUserCache);

    final ContestUserUpdater contestUserUpdater = new ContestUserUpdater(
        flameDispatcher,
        networkServerFacade,
        networkPlayerCache,
        contestUserRepository,
        redisMessenger
    );

    final ContestUserFacade contestUserFacade = new ContestUserFacade(
        contestUserCache,
        contestUserRepository,
        contestUserUpdater
    );

    final Server server = plugin.getServer();
    final BukkitScheduler scheduler = server.getScheduler();
    scheduler.runTaskTimerAsynchronously(plugin, contestUserSaver, 0L, 20 * 60L);

    final NetworkServer current = networkServerFacade.getCurrent();
    redisMessenger.subscribe(current.getName(), new ContestUserUpdateHandler(contestUserFacade));

    final PluginManager pluginManager = server.getPluginManager();
    pluginManager.registerEvents(
        new ContestUserListener(
            flameDispatcher,
            server.getPluginManager(),
            contestUserCache,
            contestUserRepository,
            contestUserFactory
        ),
        plugin
    );

    return contestUserFacade;
  }

}

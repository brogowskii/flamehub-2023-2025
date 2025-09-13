package io.github.flamehub.player.sync.data;

import dev.morphia.Datastore;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.redis.RedisService;
import io.github.flamehub.commons.server.NetworkServerFacade;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicesManager;

public final class PlayerSyncDataFacadeCreator {

  public static PlayerSyncDataFacade create(
      final Plugin plugin,
      final FlameDispatcher flameDispatcher,
      final RedisService redisService,
      final RedisMessenger redisMessenger,
      final NetworkServerFacade networkServerFacade,
      final Datastore datastore,
      final ServicesManager servicesManager,
      final Server server,
      final NetworkMessageService networkMessageService
  ) {

    return null;
  }

}

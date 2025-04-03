package io.github.flamehub.player.sync.data;

import static io.github.flamehub.player.sync.PlayerSyncConstants.PUB_SUB_CHANNEL;

import dev.morphia.Datastore;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.redis.RedisService;
import io.github.flamehub.commons.redis.storage.RedisStorage;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

public final class PlayerSyncDataFacadeCreator {

  public static PlayerSyncDataFacade create(
      final Plugin plugin,
      final FlameDispatcher flameDispatcher,
      final RedisService redisService,
      final RedisMessenger redisMessenger,
      final NetworkServerCache networkServerCache,
      final Datastore datastore,
      final ServicesManager servicesManager,
      final Server server,
      NetworkMessageService networkMessageService
  ) {

    return  null;
  }

}

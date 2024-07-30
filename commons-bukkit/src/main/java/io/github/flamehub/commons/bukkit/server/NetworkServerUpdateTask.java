package io.github.flamehub.commons.bukkit.server;

import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.commons.server.NetworkServerUpdate;
import org.bukkit.Bukkit;

public final class NetworkServerUpdateTask implements Runnable {

  private final RedisMessenger redisMessenger;
  private final NetworkServerCache networkServerCache;

  public NetworkServerUpdateTask(RedisMessenger redisMessenger,
      NetworkServerCache networkServerCache) {
    this.redisMessenger = redisMessenger;
    this.networkServerCache = networkServerCache;
  }

  @Override
  public void run() {
    NetworkServer current = this.networkServerCache.getCurrent();
    NetworkServerUpdate networkServerUpdate = new NetworkServerUpdate(
        current.getName(),
        Bukkit.getOnlinePlayers().size(),
        current.getStatistics().getPlayersLimit(),
        current.getStatistics().isFrozen(),
        Bukkit.getTPS()
    );
    this.redisMessenger.publish("network_servers", networkServerUpdate);
  }
}

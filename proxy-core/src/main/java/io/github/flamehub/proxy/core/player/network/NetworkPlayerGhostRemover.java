package io.github.flamehub.proxy.core.player.network;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.server.NetworkServerCache;
import java.util.Optional;
import java.util.logging.Logger;

public final class NetworkPlayerGhostRemover implements Runnable {

  private final Logger logger;
  private final ProxyServer proxyServer;
  private final NetworkServerCache networkServerCache;
  private final NetworkPlayerCache networkPlayerCache;

  public NetworkPlayerGhostRemover(
      Logger logger,
      ProxyServer proxyServer,
      NetworkServerCache networkServerCache,
      NetworkPlayerCache networkPlayerCache
  ) {
    this.logger = logger;
    this.proxyServer = proxyServer;
    this.networkServerCache = networkServerCache;
    this.networkPlayerCache = networkPlayerCache;
  }

  @Override
  public void run() {
    for (NetworkPlayer networkPlayer : networkPlayerCache.values()) {

      if (!networkPlayer.getProxy()
          .equalsIgnoreCase(networkServerCache.getCurrent().getName())) {
        continue;
      }

      Optional<Player> player = proxyServer.getPlayer(networkPlayer.getUniqueId());
      if (player.isEmpty()) {
//        networkPlayerCache.delete(networkPlayer);
        logger.warning("DELETED 1 GHOST PLAYER: " + networkPlayer.getName() + " : "
            + networkPlayer.getUniqueId());
      }

    }
  }
}

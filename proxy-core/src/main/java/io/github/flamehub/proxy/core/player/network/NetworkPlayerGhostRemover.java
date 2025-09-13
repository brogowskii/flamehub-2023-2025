package io.github.flamehub.proxy.core.player.network;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.server.NetworkServerFacade;
import java.util.Optional;
import java.util.logging.Logger;

public final class NetworkPlayerGhostRemover implements Runnable {

  private final Logger logger;
  private final ProxyServer proxyServer;
  private final NetworkServerFacade networkServerFacade;
  private final NetworkPlayerCache networkPlayerCache;

  public NetworkPlayerGhostRemover(
      final Logger logger,
      final ProxyServer proxyServer,
      final NetworkServerFacade networkServerFacade,
      final NetworkPlayerCache networkPlayerCache
  ) {
    this.logger = logger;
    this.proxyServer = proxyServer;
    this.networkServerFacade = networkServerFacade;
    this.networkPlayerCache = networkPlayerCache;
  }

  @Override
  public void run() {
    for (final NetworkPlayer networkPlayer : networkPlayerCache.values()) {

      if (!networkPlayer.getProxy()
          .equalsIgnoreCase(networkServerFacade.getCurrent().getName())) {
        continue;
      }

      final Optional<Player> player = proxyServer.getPlayer(networkPlayer.getUniqueId());
      if (player.isEmpty()) {
        networkPlayerCache.delete(networkPlayer);
        logger.warning("DELETED 1 GHOST PLAYER: " + networkPlayer.getName() + " : "
            + networkPlayer.getUniqueId());
      }

    }
  }
}

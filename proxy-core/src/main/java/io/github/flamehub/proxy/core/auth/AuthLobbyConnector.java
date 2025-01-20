package io.github.flamehub.proxy.core.auth;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.proxy.core.ProxyMessages;

public final class AuthLobbyConnector {

  private final ProxyServer proxyServer;
  private final ProxyMessages proxyMessages;
  private final NetworkServerCache networkServerCache;

  public AuthLobbyConnector(
      final ProxyServer proxyServer,
      final NetworkServerCache networkServerCache,
      final ProxyMessages proxyMessages
  ) {
    this.proxyServer = proxyServer;
    this.networkServerCache = networkServerCache;
    this.proxyMessages = proxyMessages;
  }

  public void findLobbyAndConnect(final Player player) {

    final NetworkServer networkServer = networkServerCache.getLeastCrowded("lobby");
    if (networkServer == null || networkServer.isOffline()) {
      player.disconnect(proxyMessages.cannotFindOnlineLobby.applyFirstAsComponent());
      return;
    }

    proxyMessages
        .attemptToConnect
        .with("server", networkServer.getName())
        .deliver(player);

    proxyServer.getServer(networkServer.getName())
        .ifPresent(registeredServer -> player
            .createConnectionRequest(registeredServer)
            .fireAndForget());

  }

}

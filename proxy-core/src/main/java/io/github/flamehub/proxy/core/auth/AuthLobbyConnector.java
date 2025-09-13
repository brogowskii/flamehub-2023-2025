package io.github.flamehub.proxy.core.auth;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerFacade;
import io.github.flamehub.proxy.core.ProxyMessages;

public final class AuthLobbyConnector {

  private final ProxyServer proxyServer;
  private final ProxyMessages proxyMessages;
  private final NetworkServerFacade networkServerFacade;

  public AuthLobbyConnector(
      final ProxyServer proxyServer,
      final NetworkServerFacade networkServerFacade,
      final ProxyMessages proxyMessages
  ) {
    this.proxyServer = proxyServer;
    this.networkServerFacade = networkServerFacade;
    this.proxyMessages = proxyMessages;
  }

  public void findLobbyAndConnect(final Player player) {

    final NetworkServer networkServer = networkServerFacade.getLeastCrowded("lobby");
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

package io.github.flamehub.proxy.core.auth;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.proxy.core.message.VelocityMessagesService;
import io.github.flamehub.proxy.core.util.TextUtil;

public final class AuthLobbyConnector {

  private final ProxyServer proxyServer;
  private final NetworkServerCache networkServerCache;
  private final VelocityMessagesService messagesService;

  public AuthLobbyConnector(ProxyServer proxyServer, NetworkServerCache networkServerCache,
      VelocityMessagesService messagesService) {
    this.proxyServer = proxyServer;
    this.networkServerCache = networkServerCache;
    this.messagesService = messagesService;
  }

  public void findLobbyAndConnect(Player player) {

    NetworkServer networkServer = networkServerCache.getLeastCrowded("lobby");
    if (networkServer == null || networkServer.isOffline()) {
      player.disconnect(
          TextUtil.parse(this.messagesService.getMessage("cannot.find.online.lobby")));
      return;
    }

    this.messagesService.message("attempt.to.connect.with.server")
        .with("server", networkServer.getName())
        .send(player);

    this.proxyServer.getServer(networkServer.getName())
        .ifPresent(registeredServer -> player
            .createConnectionRequest(registeredServer)
            .fireAndForget());

  }

}

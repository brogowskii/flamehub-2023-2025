package io.github.flamehub.proxy.core.redirect;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github.flamehub.commons.messenger.packet.PacketHandler;
import io.github.flamehub.commons.redirect.RedirectPacket;
import java.util.Optional;

public final class RedirectHandler {

  private final ProxyServer proxyServer;

  public RedirectHandler(final ProxyServer proxyServer) {
    this.proxyServer = proxyServer;
  }

  @PacketHandler
  public void handle(final RedirectPacket packet) {
    proxyServer.getPlayer(packet.getPlayer()).ifPresent(player -> {

      final Optional<RegisteredServer> server = proxyServer.getServer(packet.getServer());
      if (server.isEmpty()) {
        return;
      }

      player.createConnectionRequest(server.get()).fireAndForget();

    });
  }

}

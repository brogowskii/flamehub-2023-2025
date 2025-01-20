package io.github.flamehub.proxy.core.player;

import com.velocitypowered.api.proxy.ProxyServer;
import io.github.flamehub.commons.messenger.packet.PacketHandler;
import io.github.flamehub.proxy.core.util.TextUtil;

public final class PlayerPacketHandler {

  private final ProxyServer proxyServer;

  public PlayerPacketHandler(final ProxyServer proxyServer) {
    this.proxyServer = proxyServer;
  }

  @PacketHandler
  public void handle(final PlayerKickPacket message) {

    proxyServer.getPlayer(message.getPlayerName())
            .ifPresent(player -> player.disconnect(TextUtil.MINI_MESSAGE.deserialize(message.getReason())));

  }

}

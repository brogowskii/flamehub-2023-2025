package io.github.flamehub.proxy.core.punishment;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.flamehub.commons.messenger.packet.PacketHandler;
import io.github.flamehub.commons.punishment.PunishmentKickPacket;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.proxy.core.message.VelocityMessage;
import io.github.flamehub.proxy.core.util.TextUtil;
import java.util.Optional;

public final class PunishmentHandler {

  private final ProxyServer proxyServer;

  public PunishmentHandler(final ProxyServer proxyServer) {
    this.proxyServer = proxyServer;
  }

  @PacketHandler
  public void handle(final PunishmentKickPacket packet) {

    final String playerNickname = packet.getPlayer();
    final String reason = packet.getReason();

    proxyServer.getPlayer(playerNickname).ifPresent(player -> {
      player.disconnect(TextUtil.parse(reason));
    });


  }

}

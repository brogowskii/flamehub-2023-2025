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
  private final NetworkServerCache networkServerCache;

  public PunishmentHandler(ProxyServer proxyServer, NetworkServerCache networkServerCache) {
    this.proxyServer = proxyServer;
    this.networkServerCache = networkServerCache;
  }

  @PacketHandler
  public void handle(PunishmentKickPacket packet) {

    String playerNickname = packet.getPlayer();
    String reason = packet.getReason();

    Optional<Player> playerOptional = this.proxyServer.getPlayer(playerNickname);
    if (playerOptional.isEmpty()) {
      return;
    }

    Player player = playerOptional.get();
    NetworkServer lobby = this.networkServerCache.getLeastCrowded("lobby");
    if (lobby == null) {
      player.disconnect(TextUtil.parse(reason));
      return;
    }

    VelocityMessage.from(reason).send(player);
    player.createConnectionRequest(proxyServer.getServer(lobby.getName()).get()).fireAndForget();


  }

}

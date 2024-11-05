package io.github.flamehub.proxy.core.queue;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.flamehub.commons.messenger.packet.PacketHandler;
import io.github.flamehub.commons.queue.QueuePlayerAddPacket;
import java.util.Optional;

public final class QueueHandler {

  private final ProxyServer proxyServer;
  private final QueueService queueService;

  public QueueHandler(final ProxyServer proxyServer, final QueueService queueService) {
    this.proxyServer = proxyServer;
    this.queueService = queueService;
  }

  @PacketHandler
  public void handle(final QueuePlayerAddPacket packet) {
    final Optional<Player> optionalPlayer = proxyServer.getPlayer(packet.getPlayer());
    if (optionalPlayer.isEmpty()) {
      return;
    }

    if (this.queueService.isWaitingInAnyQueue(packet.getPlayer())) {
      return;
    }

    final Player player = optionalPlayer.get();
    player.createConnectionRequest(this.proxyServer.getServer("queue").get()).fireAndForget();

    final Queue queue = queueService.getOrCreate(packet.getServer());
    queue.addEntry(packet.getPlayer());
  }

}

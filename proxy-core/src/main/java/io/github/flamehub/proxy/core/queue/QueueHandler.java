package io.github.flamehub.proxy.core.queue;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.flamehub.commons.messenger.packet.PacketHandler;
import io.github.flamehub.commons.queue.QueuePlayerAddPacket;

import java.util.Optional;

public final class QueueHandler {

    private final ProxyServer proxyServer;
    private final QueueService queueService;

    public QueueHandler(ProxyServer proxyServer, QueueService queueService) {
        this.proxyServer = proxyServer;
        this.queueService = queueService;
    }

    @PacketHandler
    public void handle(QueuePlayerAddPacket packet) {
        Optional<Player> optionalPlayer = this.proxyServer.getPlayer(packet.getPlayer());
        if (optionalPlayer.isEmpty()) {
            return;
        }

        Player player = optionalPlayer.get();
        player.createConnectionRequest(this.proxyServer.getServer("queue").get()).fireAndForget();
        this.queueService.add(packet.getServer(), packet.getPlayer());
    }

}

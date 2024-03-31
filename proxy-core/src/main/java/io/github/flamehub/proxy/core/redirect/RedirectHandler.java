package io.github.flamehub.proxy.core.redirect;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github.flamehub.commons.messenger.packet.PacketHandler;
import io.github.flamehub.commons.redirect.RedirectPacket;

import java.util.Optional;

public final class RedirectHandler {

    private final ProxyServer proxyServer;

    public RedirectHandler(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @PacketHandler
    public void handle(RedirectPacket packet) {
        Optional<Player> optionalPlayer = proxyServer.getPlayer(packet.getPlayer());
        optionalPlayer.ifPresent(player -> {

            Optional<RegisteredServer> server = this.proxyServer.getServer(packet.getServer());
            if (server.isEmpty()) {
                return;
            }

            player.createConnectionRequest(server.get()).fireAndForget();

        });
    }

}

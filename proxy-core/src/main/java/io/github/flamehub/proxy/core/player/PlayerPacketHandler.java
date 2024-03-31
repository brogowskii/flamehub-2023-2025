package io.github.flamehub.proxy.core.player;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.flamehub.commons.messenger.packet.PacketHandler;
import io.github.flamehub.proxy.core.text.TextUtil;

import java.util.Optional;

public final class PlayerPacketHandler {

    private final ProxyServer proxyServer;

    public PlayerPacketHandler(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @PacketHandler
    public void handle(PlayerKickPacket Message) {

        Optional<Player> optionalPlayer = this.proxyServer.getPlayer(Message.getPlayerName());
        optionalPlayer.ifPresent(player -> player.disconnect(TextUtil.parse(Message.getReason())));

    }

}

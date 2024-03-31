package io.github.flamehub.proxy.core.queue;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import io.github.flamehub.proxy.core.text.TextUtil;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;
import net.kyori.adventure.text.Component;

import java.util.Optional;

public class QueueListener {

    private final NetworkServerCache networkServerCache;
    private final QueueService queueService;
    private final ProxyServer proxyServer;

    public QueueListener(NetworkServerCache networkServerCache, QueueService queueService, ProxyServer proxyServer) {
        this.networkServerCache = networkServerCache;
        this.queueService = queueService;
        this.proxyServer = proxyServer;
    }

    @Subscribe
    public void onQuit(KickedFromServerEvent event) {
        if (event.kickedDuringServerConnect()) {
            return;
        }

        if (event.getServerKickReason().isPresent()) {

            Component component = event.getServerKickReason().get();
            String serialize = TextUtil.serialize(component);
            if (serialize.contains("ban")) {
                return;
            }

        }

        RegisteredServer server = event.getServer();
        if (server == null) {
            return;
        }

        ServerInfo serverInfo = server.getServerInfo();
        if (serverInfo.getName().equals("auth") || serverInfo.getName().equals("queue")) {
            return;
        }

        Optional<NetworkServer> optionalNetworkServer = this.networkServerCache.findByName(serverInfo.getName());
        optionalNetworkServer.ifPresent(networkServer -> {

            Player player = event.getPlayer();
            this.queueService.add(networkServer.getCategory(), player.getUsername());
            KickedFromServerEvent.ServerKickResult kickResult = KickedFromServerEvent.RedirectPlayer.create(this.proxyServer.getServer("queue").get());
            event.setResult(kickResult);

        });

    }

    @Subscribe
    public void onConnect(ServerConnectedEvent event) {
        Optional<RegisteredServer> optionalPreviousServer = event.getPreviousServer();
        if (optionalPreviousServer.isEmpty()) {
            return;
        }

        RegisteredServer previousServer = optionalPreviousServer.get();
        if (previousServer.getServerInfo().getName().equals("queue")) {
            this.queueService.remove(event.getPlayer().getUsername());
        }

    }

}

package io.github.flamehub.proxy.core.queue;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import io.github.flamehub.proxy.core.ProxyCore;
import io.github.flamehub.proxy.core.message.VelocityMessage;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class QueueListener {

    private final NetworkServerCache networkServerCache;
    private final QueueService queueService;
    private final ProxyServer proxyServer;
    private final ProxyCore proxyCore;

    public QueueListener(NetworkServerCache networkServerCache, QueueService queueService, ProxyServer proxyServer, ProxyCore proxyCore) {
        this.networkServerCache = networkServerCache;
        this.queueService = queueService;
        this.proxyServer = proxyServer;
        this.proxyCore = proxyCore;
    }

    @Subscribe
    public void onQuit(KickedFromServerEvent event) {
        if (event.kickedDuringServerConnect()) {
            return;
        }


        RegisteredServer server = event.getServer();
        ServerInfo serverInfo = server.getServerInfo();
        if (serverInfo.getName().equals("auth") || serverInfo.getName().equals("queue")) {
            return;
        }

        Optional<NetworkServer> optionalNetworkServer = this.networkServerCache.findByName(serverInfo.getName());
        optionalNetworkServer.ifPresent(networkServer -> {

            Player player = event.getPlayer();
            VelocityMessage.from(
                    "",
                    "&cUtracono połączenie z serwerem &4" + networkServer.getName() + "&c!",
                    "&bŁączę z kolejką &3" + networkServer.getCategory() + "&b...",
                    ""
            ).send(player);

            KickedFromServerEvent.ServerKickResult kickResult = KickedFromServerEvent.RedirectPlayer.create(this.proxyServer.getServer("queue").get());
            event.setResult(kickResult);
            this.proxyServer.getScheduler()
                    .buildTask(this.proxyCore, () -> this.queueService.add(networkServer.getCategory(), player.getUsername()))
                    .delay(4, TimeUnit.SECONDS)
                    .schedule();

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

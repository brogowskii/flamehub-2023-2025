package io.github.flamehub.proxy.core.queue;

import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.flamehub.proxy.core.text.TextUtil;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

import java.time.Duration;
import java.util.Optional;

public class QueueRedirectService {

    private final ProxyServer proxyServer;
    private final QueueService queueService;
    private final NetworkServerCache networkServerCache;

    public QueueRedirectService(ProxyServer proxyServer, QueueService queueService, NetworkServerCache networkServerCache) {
        this.proxyServer = proxyServer;
        this.queueService = queueService;
        this.networkServerCache = networkServerCache;
    }

    public boolean move(String queuePlayer, String queue) {
        Optional<Player> optionalPlayer = this.proxyServer.getPlayer(queuePlayer);
        if (optionalPlayer.isEmpty()) {
            this.queueService.remove(queue, queuePlayer);
            return false;
        }

        Player player = optionalPlayer.get();
        NetworkServer leastCrowded = this.networkServerCache.getLeastCrowded(queue);
        if (leastCrowded == null) {
            player.showTitle(Title.title(TextUtil.parse(""), TextUtil.parse("&cBrak serwera w kategorii: &4" + queue), Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(3), Duration.ofSeconds(1))));
            player.sendMessage(TextUtil.parse("&cNie ma żadnego wolnego serwera w kategorii &4" + queue));
            return false;
        }

        if (leastCrowded.isOffline()) {
            player.showTitle(Title.title(TextUtil.parse(""), TextUtil.parse("&cSerwer docelowy &4" + leastCrowded.getName() + " &cjest aktualnie offline!"), Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(3), Duration.ofSeconds(1))));
            player.sendMessage(TextUtil.parse("&cSerwer z którym próbujesz się połączyć jest aktualnie &4offline&c! Szukam wolnego serwera..."));
            return false;
        }

        if (leastCrowded.getStatistics().getPlayers() >= leastCrowded.getStatistics().getPlayersLimit()) {
            player.showTitle(Title.title(TextUtil.parse(""), TextUtil.parse("&cSerwer &4" + leastCrowded.getName() + " &cjest prawdopodobnie pełen graczy!"), Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(3), Duration.ofSeconds(1))));
            player.sendMessage(TextUtil.parse("&cSerwer z którym próbujesz się połączyć jest prawdopodobnie pełen graczy! Szukam wolnego serwera..."));
            return false;
        }

        this.proxyServer.getServer(leastCrowded.getName()).ifPresent(registeredServer -> {
            player.createConnectionRequest(registeredServer)
                    .connect()
                    .whenComplete((result, throwable) -> {
                        queueService.remove(player.getUsername());
                        if (result.getStatus() != ConnectionRequestBuilder.Status.SUCCESS) {
                            Component message = result.getReasonComponent().get();
                            player.sendMessage(message);
                            NetworkServer leastCrowdedLobby = this.networkServerCache.getLeastCrowded("lobby");
                            if (leastCrowdedLobby == null) {
                                player.disconnect(TextUtil.parse("&cWystąpił błąd"));
                                return;
                            }

                            player.createConnectionRequest(this.proxyServer.getServer(leastCrowdedLobby.getName()).get()).fireAndForget();

                        }



                    });
        });

        return true;
    }

}

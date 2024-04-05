package io.github.flamehub.proxy.core.queue;

import com.velocitypowered.api.proxy.ProxyServer;
import io.github.flamehub.proxy.core.util.TextUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class QueueInfoTask implements Runnable {

    private final ProxyServer proxyServer;
    private final QueueService queueService;

    public QueueInfoTask(ProxyServer proxyServer, QueueService queueService) {
        this.proxyServer = proxyServer;
        this.queueService = queueService;
    }

    @Override
    public void run() {

        for (String entry : this.queueService.getQueues().keySet()) {
            List<String> playersFromQueue = this.queueService.findPlayersFromQueue(entry);

            for (String s : playersFromQueue) {
                if (s == null) {
                    continue;
                }
                if (this.proxyServer.getPlayer(s).isEmpty()) {
                    System.out.println("removed: " + s);
                    this.queueService.remove(s);
                }
            }

            playersFromQueue.stream()
                    .map(this.proxyServer::getPlayer)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(player -> {
                        int position = this.queueService.getPlace(entry, player.getUsername());
                        player.sendActionBar(TextUtil.parse("&7Kolejka do: &6" + entry + " &8| &7Twoja pozycja: &e" + (position + 1) + "&8/&6" + playersFromQueue.size()));

                    });


        }
    }
}

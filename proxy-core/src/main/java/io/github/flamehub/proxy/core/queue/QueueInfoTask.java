package io.github.flamehub.proxy.core.queue;

import com.velocitypowered.api.proxy.ProxyServer;
import io.github.flamehub.proxy.core.text.TextUtil;

import java.util.LinkedList;
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

        for (Map.Entry<String, LinkedList<String>> entry : this.queueService.getQueues().entrySet()) {
            String key = entry.getKey();
            LinkedList<String> value = entry.getValue();

            value.stream()
                    .map(this.proxyServer::getPlayer)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(player -> {

                        int position = this.queueService.getPlace(key, player.getUsername());
                        player.sendActionBar(TextUtil.parse("&7Kolejka do: &6" + key + " &8| &7Twoja pozycja: &e" + (position + 1) + "&8/&6" + value.size()));

                    });


        }
    }
}

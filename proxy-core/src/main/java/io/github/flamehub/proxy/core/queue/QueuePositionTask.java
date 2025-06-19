package io.github.flamehub.proxy.core.queue;

import com.velocitypowered.api.proxy.ProxyServer;
import io.github.flamehub.proxy.core.util.TextUtil;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

public final class QueuePositionTask implements Runnable {

  private final ProxyServer proxyServer;
  private final QueueService queueService;

  public QueuePositionTask(final ProxyServer proxyServer, final QueueService queueService) {
    this.proxyServer = proxyServer;
    this.queueService = queueService;
  }

  @Override
  public void run() {

    queueService.getQueues().forEach(this::process);
  }

  void process(final Queue queue) {
    final LinkedBlockingQueue<String> entries = queue.getEntries();
    new ArrayList<>(entries).stream()
        .filter(player -> proxyServer.getPlayer(player).isEmpty())
        .forEach(queue::removeEntry);

    entries.stream()
        .map(proxyServer::getPlayer)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(player -> {
          final int place = queue.getPlace(player.getUsername());
          final int totalPlayers = entries.size();

          final String actionBarMessage = String.format(
              "&7Kolejka do: &6%s &8| &7Twoja pozycja: &e%d&8",
              queue.getName(),
              place + 1
          );
          player.sendActionBar(TextUtil.parse(actionBarMessage));
        });

  }
}

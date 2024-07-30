package io.github.flamehub.proxy.core.queue;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.flamehub.proxy.core.util.TextUtil;
import java.util.List;
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
      if (entry == null) {
        continue;
      }

      List<String> playersFromQueue = this.queueService.findPlayersFromQueue(entry);

      if (playersFromQueue == null || playersFromQueue.isEmpty()) {
        continue;
      }

      for (String s : playersFromQueue) {
        if (s == null || s.isEmpty()) {
          continue;
        }

        Optional<Player> optionalPlayer = this.proxyServer.getPlayer(s);
        if (optionalPlayer.isEmpty()) {
          System.out.println("removed: " + s);
          this.queueService.remove(s);
          continue;
        }

        Player player = optionalPlayer.get();
        int position = this.queueService.getPlace(entry, player.getUsername());
        player.sendActionBar(TextUtil.parse(
            "&7Kolejka do: &6" + entry + " &8| &7Twoja pozycja: &e" + (position + 1) + "&8/&6"
                + playersFromQueue.size()));

      }

    }
  }
}

package io.github.flamehub.commons.bukkit.tab;

import io.github.flamehub.commons.bukkit.CommonsPlugin;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class TablistTask implements Runnable {

  private final Queue<Player> toUpdate = new ConcurrentLinkedQueue<>();

  public TablistTask(final TablistService tablistService) {
    Bukkit.getScheduler()
        .runTaskTimerAsynchronously(CommonsPlugin.getInstance(), () -> {
          Player player;
          while ((player = toUpdate.poll()) != null) {
            tablistService.send(player);
          }
        }, 2L, 2L);

  }

  @Override
  public void run() {
    toUpdate.addAll(Bukkit.getOnlinePlayers());
  }
}

package io.github.flamehub.commons.bukkit.tab;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class TablistTask implements Runnable {

  private final TablistService tablistService;

  public TablistTask(TablistService tablistService) {
    this.tablistService = tablistService;
  }

  @Override
  public void run() {
    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      this.tablistService.send(onlinePlayer);
    }
  }
}

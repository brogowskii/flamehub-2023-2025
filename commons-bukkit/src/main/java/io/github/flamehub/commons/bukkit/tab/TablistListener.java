package io.github.flamehub.commons.bukkit.tab;

import io.github.flamehub.commons.bukkit.CommonsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class TablistListener implements Listener {

  private final TablistService tablistService;

  public TablistListener(final TablistService tablistService) {
    this.tablistService = tablistService;
  }

  @EventHandler
  public void onQuit(final PlayerQuitEvent event) {
    final Player player = event.getPlayer();
    if (tablistService instanceof final PageableTablistService pageableTablistService) {
      pageableTablistService.clearAndRemoveEntries(player);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onJoin(final PlayerJoinEvent e) {
    if (tablistService instanceof final PageableTablistService pageableTablistService) {
      Bukkit.getScheduler().runTaskLaterAsynchronously(CommonsPlugin.getInstance(), () -> {
        pageableTablistService.firstInit(e.getPlayer());
        pageableTablistService.send(e.getPlayer());
      }, 10L);
    }
  }
}

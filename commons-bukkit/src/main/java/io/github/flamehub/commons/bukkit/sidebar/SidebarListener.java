package io.github.flamehub.commons.bukkit.sidebar;

import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class SidebarListener implements Listener {

  private final SidebarCache sidebarCache;

  public SidebarListener(final SidebarCache sidebarCache) {
    this.sidebarCache = sidebarCache;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onJoin(final PlayerJoinEvent event) {
    final FastBoard fastBoard = new FastBoard(event.getPlayer());
    sidebarCache.add(event.getPlayer().getUniqueId(), fastBoard);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onQuit(final PlayerQuitEvent event) {
    final Player player = event.getPlayer();
    final FastBoard fastBoard = sidebarCache.findByKey(player.getUniqueId());
    if (fastBoard != null) {
      try {
        fastBoard.delete();
      } catch (final Exception ignored) {
      } finally {
        sidebarCache.remove(player.getUniqueId());
      }
    }
  }
}
package io.github.flamehub.lobby.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

public final class TabCompleteListener implements Listener {

  @EventHandler(priority = EventPriority.LOWEST)
  public void tabProtect(PlayerCommandSendEvent event) {
    if (!event.getPlayer().hasPermission("commands.execute")) {
      event.getCommands().clear();
    }
  }

  @EventHandler
  public void onCommand(PlayerCommandPreprocessEvent event) {
    if (!event.getPlayer().hasPermission("commands.execute")) {
      if (event.getMessage().startsWith("/joinserver") || event.getMessage()
          .startsWith("/portfel")) {
        return;
      }
      event.setCancelled(true);
    }
  }

}

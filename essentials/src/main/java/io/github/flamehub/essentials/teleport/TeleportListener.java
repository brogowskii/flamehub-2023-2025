package io.github.flamehub.essentials.teleport;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

final class TeleportListener implements Listener {

  private final FlameDispatcher flameDispatcher;
  private final TeleportFacade teleportFacade;

  TeleportListener(final FlameDispatcher flameDispatcher, final TeleportFacade teleportFacade) {
    this.flameDispatcher = flameDispatcher;
    this.teleportFacade = teleportFacade;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onJoin(final PlayerJoinEvent event) {
    Player player = event.getPlayer();
    String teleportTarget = this.teleportFacade.getTeleportTarget(player.getUniqueId());
    if (teleportTarget == null) {
      return;
    }

    this.flameDispatcher.dispatchLater(() -> {
      Player target = Bukkit.getPlayer(teleportTarget);
      if (target != null) {
        player.teleport(target);
      }

      this.teleportFacade.remove(player.getUniqueId());
    }, 7L);

  }

}

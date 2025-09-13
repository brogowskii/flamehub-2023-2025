package io.github.flamehub.kits.kit;

import io.github.flamehub.commons.bukkit.util.InventoryUtil;
import io.github.flamehub.kits.KitsConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public final class KitListener implements Listener {

  private final KitsConfig kitsConfig;

  public KitListener(final KitsConfig kitsConfig) {
    this.kitsConfig = kitsConfig;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onJoin(final PlayerJoinEvent event) {

    final Player player = event.getPlayer();
    if (!player.hasPlayedBefore()) {
      final Kit kit = kitsConfig.findByName(kitsConfig.getStarterKit());
      if (kit == null) {
        return;
      }

      InventoryUtil.addItems(player, kit.getItems());
    }

  }

  @EventHandler
  public void onRespawn(final PlayerRespawnEvent event) {
    final Kit kit = kitsConfig.findByName(kitsConfig.getStarterKit());
    if (kit == null) {
      return;
    }

    InventoryUtil.addItems(event.getPlayer(), kit.getItems());
  }

}

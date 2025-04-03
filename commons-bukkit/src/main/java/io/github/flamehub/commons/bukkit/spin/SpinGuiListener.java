package io.github.flamehub.commons.bukkit.spin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public final class SpinGuiListener implements Listener {

  @EventHandler
  public void onClick(final InventoryClickEvent event) {
    final Inventory inventory = event.getInventory();
    if (inventory.getHolder() instanceof SpinGuiHolder) {
      event.setCancelled(true);
    }
  }

}

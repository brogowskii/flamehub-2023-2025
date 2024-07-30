package io.github.flamehub.kits.kit.management;

import io.github.flamehub.commons.config.FlameConfigService;
import io.github.flamehub.kits.KitsConfig;
import io.github.flamehub.kits.kit.Kit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public final class KitManagementListener implements Listener {

  private final FlameConfigService flameConfigService;
  private final KitsConfig kitsConfig;

  public KitManagementListener(FlameConfigService flameConfigService, KitsConfig kitsConfig) {
    this.flameConfigService = flameConfigService;
    this.kitsConfig = kitsConfig;
  }

  @EventHandler
  public void onClose(InventoryCloseEvent event) {

    InventoryView view = event.getView();
    String titleString = ChatColor.stripColor(view.getTitle());
    if (titleString.startsWith("Edytor zestawu")) {

      String[] split = titleString.split(": ");
      String kit = split[1];
      Kit byName = this.kitsConfig.findByName(kit);
      byName.getItems().clear();
      for (ItemStack itemStack : event.getInventory().getContents()) {
        if (itemStack == null) {
          continue;
        }

        byName.getItems().add(itemStack);
      }

      this.flameConfigService.saveLocally(KitsConfig.class);
    }

  }

}

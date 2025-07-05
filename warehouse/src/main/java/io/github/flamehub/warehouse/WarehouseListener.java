package io.github.flamehub.warehouse;

import io.github.flamehub.commons.bukkit.util.SerializationUtil;
import io.github.flamehub.warehouse.user.WarehouseUser;
import io.github.flamehub.warehouse.user.WarehouseUserCache;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public final class WarehouseListener implements Listener {

  private final WarehouseUserCache warehouseUserCache;

  public WarehouseListener(final WarehouseUserCache warehouseUserCache) {
    this.warehouseUserCache = warehouseUserCache;
  }

  @EventHandler
  public void onInventoryClose(final InventoryCloseEvent event) {

    final HumanEntity humanEntity = event.getPlayer();
    if (!(humanEntity instanceof Player)) {
      return;
    }

    if (event.getInventory().getHolder() instanceof final WarehouseHolder warehouseHolder) {
      final WarehouseUser coreUser = warehouseUserCache.findByKey(warehouseHolder.getOwner());
      final Warehouse warehouse = coreUser.getWarehouseMap().get(warehouseHolder.getId());
      if (warehouse != null) {
        warehouse.setSerializedContents(
            SerializationUtil.serializeBukkitObject(event.getInventory().getContents()));
        coreUser.markToUpdate();
      }
    }
  }

}

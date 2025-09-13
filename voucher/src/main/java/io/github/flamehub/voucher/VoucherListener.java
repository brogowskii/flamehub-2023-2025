package io.github.flamehub.voucher;

import io.github.flamehub.player.sync.data.PlayerDataLoadSyncEvent;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public final class VoucherListener implements Listener {

  private final VoucherService voucherService;

  public VoucherListener(final VoucherService voucherService) {
    this.voucherService = voucherService;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onInteract(final PlayerInteractEvent event) {

    if (event.getHand() != EquipmentSlot.HAND) {
      return;
    }

    if (event.getAction() == Action.RIGHT_CLICK_AIR
        || event.getAction() == Action.RIGHT_CLICK_BLOCK) {

      final Player player = event.getPlayer();
      final ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
      final ItemMeta itemMeta = itemInMainHand.getItemMeta();
      if (itemMeta == null) {
        return;
      }

      if (itemMeta.getPersistentDataContainer().has(voucherService.getKey())) {
        voucherService.useVoucher(player, itemInMainHand);
        event.setCancelled(true);
      }

    }

  }

}

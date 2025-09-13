package io.github.flamehub.commons.bukkit.util;

import java.util.Collection;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class InventoryUtil {

  private InventoryUtil() {

  }

  public static void addItem(final Player player, final ItemStack item) {
    if (item == null) {
      return;
    }

    final Map<Integer, ItemStack> leftOver = player.getInventory().addItem(item.clone());
    for (final ItemStack leftoverItem : leftOver.values()) {
      player.getWorld().dropItem(player.getLocation(), leftoverItem.clone());
    }
  }

  public static void addItems(final Player player, final Collection<ItemStack> items) {

    final Map<Integer, ItemStack> leftOver = player.getInventory()
        .addItem(items.toArray(new ItemStack[0]));

    for (final Map.Entry<Integer, ItemStack> en : leftOver.entrySet()) {
      player.getWorld().dropItemNaturally(player.getLocation(), en.getValue());
    }
  }

  public static void addItems(final Player player, final Collection<ItemStack> items, final Block block) {

    final Map<Integer, ItemStack> leftOver = player.getInventory()
        .addItem(items.toArray(new ItemStack[0]));
    for (final Map.Entry<Integer, ItemStack> en : leftOver.entrySet()) {
      block.getWorld().dropItemNaturally(block.getLocation(), en.getValue());
    }
  }

  public static void removeItem(final Player player, final ItemStack item) {
    if (item == null || item.getType() == Material.AIR) {
      return;
    }

    player.getInventory().removeItem(item);
  }

}

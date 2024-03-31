package io.github.flamehub.commons.bukkit.util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;

public final class InventoryUtil {

    private InventoryUtil() {

    }

    public static void addItem(Player player, ItemStack item) {
        if (item == null) {
            return;
        }

        Map<Integer, ItemStack> leftOver = player.getInventory().addItem(item.clone());
        for (ItemStack leftoverItem : leftOver.values()) {
            player.getWorld().dropItem(player.getLocation(), leftoverItem.clone());
        }
    }

    public static void addItems(Player player, Collection<ItemStack> items) {

        Map<Integer, ItemStack> leftOver = player.getInventory().addItem(items.toArray(new ItemStack[0]));

        for (Map.Entry<Integer, ItemStack> en : leftOver.entrySet()) {
            player.getWorld().dropItemNaturally(player.getLocation(), en.getValue());
        }
    }

    public static void addItems(Player player, Collection<ItemStack> items, Block block) {

        Map<Integer, ItemStack> leftOver = player.getInventory().addItem(items.toArray(new ItemStack[0]));
        for (Map.Entry<Integer, ItemStack> en : leftOver.entrySet()) {
            block.getWorld().dropItemNaturally(block.getLocation(), en.getValue());
        }
    }

    public static void removeItem(Player player, ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        player.getInventory().removeItem(item);
    }

}

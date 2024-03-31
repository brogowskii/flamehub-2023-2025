package io.github.flamehub.scratch;

import org.bukkit.inventory.ItemStack;

import java.io.Serializable;

public final class ScratchDrop implements Serializable {

    private final String friendlyName;
    private final ItemStack itemStack;
    private final double chance;

    public ScratchDrop(ItemStack itemStack, double chance) {
        this.friendlyName = itemStack.getType().toString().toUpperCase();
        this.itemStack = itemStack;
        this.chance = chance;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public double getChance() {
        return chance;
    }
}

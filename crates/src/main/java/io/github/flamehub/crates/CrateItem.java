package io.github.flamehub.crates;

import org.bukkit.inventory.ItemStack;

import java.io.Serializable;

public final class CrateItem implements Serializable {

    private final String friendlyName;

    private final ItemStack itemStack;
    private final double chance;
    private final int value;

    public CrateItem(String friendlyName, ItemStack itemStack, double chance, int value) {
        this.friendlyName = friendlyName;
        this.itemStack = itemStack;
        this.chance = chance;
        this.value = value;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public double getChance() {
        return chance;
    }

    public int getValue() {
        return value;
    }
}

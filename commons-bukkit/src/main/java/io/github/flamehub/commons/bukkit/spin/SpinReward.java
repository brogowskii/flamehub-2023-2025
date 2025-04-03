package io.github.flamehub.commons.bukkit.spin;

import org.bukkit.inventory.ItemStack;

public final class SpinReward {

  private final ItemStack itemStack;
  private final double chance;

  public SpinReward(final ItemStack itemStack, final double chance) {
    this.itemStack = itemStack;
    this.chance = chance;
  }

  public ItemStack getItemStack() {
    return itemStack;
  }

  public double getChance() {
    return chance;
  }
}

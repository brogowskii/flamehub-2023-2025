package io.github.flamehub.flamebox;

import java.io.Serializable;
import org.bukkit.inventory.ItemStack;

public final class FlameBoxDrop implements Serializable {

  private String friendlyName;
  private ItemStack itemStack;
  private double chance;

  public FlameBoxDrop() {
  }

  public FlameBoxDrop(final ItemStack itemStack, final double chance) {
    friendlyName = itemStack.getType().toString().toUpperCase();
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

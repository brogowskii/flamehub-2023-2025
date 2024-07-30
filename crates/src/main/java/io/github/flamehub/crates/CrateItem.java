package io.github.flamehub.crates;

import java.io.Serializable;
import org.bukkit.inventory.ItemStack;

public final class CrateItem implements Serializable {

  private String friendlyName;

  private ItemStack itemStack;
  private double chance;
  private int value;

  public CrateItem(String friendlyName, ItemStack itemStack, double chance, int value) {
    this.friendlyName = friendlyName;
    this.itemStack = itemStack;
    this.chance = chance;
    this.value = value;
  }

  public CrateItem() {
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

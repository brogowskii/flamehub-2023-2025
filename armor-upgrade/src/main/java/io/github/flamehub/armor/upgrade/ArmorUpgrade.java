package io.github.flamehub.armor.upgrade;

import java.io.Serializable;
import org.bukkit.inventory.ItemStack;

public final class ArmorUpgrade implements Serializable {

  private ArmorUpgradeType type;
  private int level;
  private double cost;
  private ItemStack itemStack;

  public ArmorUpgrade() {
  }

  public ArmorUpgrade(final ArmorUpgradeType type, final int level, final double cost,
      final ItemStack itemStack) {
    this.type = type;
    this.level = level;
    this.cost = cost;
    this.itemStack = itemStack;
  }

  public int getLevel() {
    return level;
  }

  public double getCost() {
    return cost;
  }

  public ItemStack getItemStack() {
    return itemStack;
  }

  public ArmorUpgradeType getType() {
    return type;
  }

  public void setItemStack(final ItemStack itemStack) {
    this.itemStack = itemStack;
  }
}

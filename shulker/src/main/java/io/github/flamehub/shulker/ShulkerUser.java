package io.github.flamehub.shulker;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class ShulkerUser {

  private final ItemStack itemStack;
  private final int slot;
  private final Material type;

  public ShulkerUser(ItemStack itemStack, int slot) {
    this.itemStack = itemStack;
    this.slot = slot;
    this.type = itemStack.getType();
  }

  public ItemStack getItemStack() {
    return itemStack;
  }

  public int getSlot() {
    return slot;
  }

  public Material getType() {
    return type;
  }

}

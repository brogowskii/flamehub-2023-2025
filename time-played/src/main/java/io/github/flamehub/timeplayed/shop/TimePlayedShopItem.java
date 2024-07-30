package io.github.flamehub.timeplayed.shop;

import java.io.Serializable;
import org.bukkit.inventory.ItemStack;

public final class TimePlayedShopItem implements Serializable {

  private ItemStack itemStack;
  private int price;

  public TimePlayedShopItem() {
  }

  public TimePlayedShopItem(ItemStack itemStack, int price) {
    this.itemStack = itemStack;
    this.price = price;
  }

  public ItemStack getItemStack() {
    return itemStack;
  }

  public int getPrice() {
    return price;
  }
}

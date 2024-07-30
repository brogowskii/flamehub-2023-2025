package io.github.flamehub.auctionhouse.commons.category;

import java.io.Serializable;
import java.util.List;

public final class AuctionHouseCategoryIcon implements Serializable {

  private String material;
  private String name;
  private List<String> lore;
  private int slot;

  public AuctionHouseCategoryIcon() {
  }

  public AuctionHouseCategoryIcon(String material, String name, List<String> lore, int slot) {
    this.material = material;
    this.name = name;
    this.lore = lore;
    this.slot = slot;
  }

  public String getMaterial() {
    return material;
  }

  public String getName() {
    return name;
  }

  public List<String> getLore() {
    return lore;
  }

  public int getSlot() {
    return slot;
  }
}

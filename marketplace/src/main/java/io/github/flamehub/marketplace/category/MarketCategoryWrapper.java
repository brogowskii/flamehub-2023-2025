package io.github.flamehub.marketplace.category;

import java.io.Serializable;
import java.util.List;
import org.bukkit.Material;

public final class MarketCategoryWrapper implements Serializable {

  private String id;
  private String name;
  private int slot;
  private Material material;
  private List<MarketCategory> marketCategories;

  public MarketCategoryWrapper(
      final String id,
      final String name,
      final int slot,
      final Material material,
      final List<MarketCategory> marketCategories) {
    this.id = id;
    this.name = name;
    this.slot = slot;
    this.material = material;
    this.marketCategories = marketCategories;
  }

  public MarketCategoryWrapper() {
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getSlot() {
    return slot;
  }

  public Material getMaterial() {
    return material;
  }

  public List<MarketCategory> getMarketCategories() {
    return marketCategories;
  }
}

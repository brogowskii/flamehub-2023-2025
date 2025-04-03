package io.github.flamehub.marketplace.category;

import java.io.Serializable;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public final class MarketCategory implements Serializable {

  private String id;
  private String friendlyName;
  private List<String> materials;

  @Nullable
  private String enchantment;

  private int customModelData;

  public MarketCategory() {

  }

  public MarketCategory(
      final String id,
      final String friendlyName,
      final List<String> materials,
      final String enchantment,
      final int customModelData) {
    this.id = id;
    this.friendlyName = friendlyName;
    this.materials = materials;
    this.enchantment = enchantment;
    this.customModelData = customModelData;
  }

  public String getId() {
    return id;
  }

  public String getFriendlyName() {
    return friendlyName;
  }

  public List<String> getMaterials() {
    return materials;
  }

  public @Nullable String getEnchantment() {
    return enchantment;
  }

  public int getCustomModelData() {
    return customModelData;
  }
}

package io.github.flamehub.ranking;

import java.io.Serializable;
import java.util.List;
import org.bukkit.Material;

public final class RankingItem implements Serializable {

  private String name;
  private String template;
  private List<String> additionalLore;
  private int slot;
  private Material material;

  public RankingItem() {
  }

  public RankingItem(
      final String name,
      final String template,
      final List<String> additionalLore,
      final int slot,
      final Material material
  ) {
    this.name = name;
    this.template = template;
    this.additionalLore = additionalLore;
    this.slot = slot;
    this.material = material;
  }

  public String getName() {
    return name;
  }

  public String getTemplate() {
    return template;
  }

  public List<String> getAdditionalLore() {
    return additionalLore;
  }

  public int getSlot() {
    return slot;
  }

  public Material getMaterial() {
    return material;
  }
}

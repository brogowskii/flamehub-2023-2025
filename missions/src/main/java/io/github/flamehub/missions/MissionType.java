package io.github.flamehub.missions;

import org.bukkit.Material;

public enum MissionType {

  BLOCK_BREAK("Zniszcz bloki", Material.NETHERITE_PICKAXE, new long[]{20000, 30000, 50000}),
  KILL("Zabij graczy", Material.NETHERITE_SWORD, new long[]{5, 10, 15, 20}),
  CLAIM_RANKING("Zdobądź ranking", Material.FISHING_ROD, new long[]{200, 300, 500}),
  DAMAGE_DEALT("Zadawaj obrażenia graczom", Material.DIAMOND_SWORD, new long[]{2500, 3000, 3500}),
  EAT_GOLDEN_APPLES("Zjedz złote jabłka", Material.GOLDEN_APPLE, new long[]{500, 1000, 1500});

  private final String description;
  private final Material icon;
  private final long[] required;

  MissionType(String description, Material icon, long[] required) {
    this.required = required;
    this.icon = icon;
    this.description = description;
  }

  public Material getIcon() {
    return icon;
  }

  public long[] getRequired() {
    return required;
  }

  public String getDescription() {
    return description;
  }
}

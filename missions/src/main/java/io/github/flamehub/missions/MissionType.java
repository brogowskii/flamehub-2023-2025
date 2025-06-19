package io.github.flamehub.missions;

import org.bukkit.Material;

public enum MissionType {

  BLOCK_BREAK("Zniszcz bloki", Material.NETHERITE_PICKAXE),
  WOOL_BREAK("Zniszcz wełne", Material.WHITE_WOOL),
  KILL("Zabij graczy", Material.NETHERITE_SWORD),
  CLAIM_RANKING("Zdobądź ranking", Material.FISHING_ROD),
  DAMAGE_DEALT("Zadawaj obrażenia graczom", Material.DIAMOND_SWORD),
  EAT_GOLDEN_APPLES("Zjedz refy", Material.GOLDEN_APPLE),
  OPEN_CRATE("Otwórz skrzynki", Material.SHULKER_BOX),
  PUMPKIN_BREAK("", Material.AIR);

  private final String description;
  private final Material icon;

  MissionType(String description, Material icon) {
    this.icon = icon;
    this.description = description;
  }

  public Material getIcon() {
    return icon;
  }

  public String getDescription() {
    return description;
  }
}

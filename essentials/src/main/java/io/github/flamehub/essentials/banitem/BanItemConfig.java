package io.github.flamehub.essentials.banitem;

import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;

@FlameConfigProperties(name = "banitem.json")
public final class BanItemConfig extends FlameConfig {

  private final List<Material> materialsBreak = new ArrayList<>(List.of(Material.BEDROCK));
  private final List<Material> materialsPlace = new ArrayList<>(List.of(Material.BEDROCK));
  private final List<Material> craftings = new ArrayList<>(List.of(Material.BEDROCK));

  public BanItemConfig() {
  }

  public List<Material> getMaterialsBreak() {
    return materialsBreak;
  }

  public List<Material> getMaterialsPlace() {
    return materialsPlace;
  }

  public List<Material> getCraftings() {
    return craftings;
  }
}

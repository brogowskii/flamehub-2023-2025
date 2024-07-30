package io.github.flamehub.essentials.warp;

import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

@FlameConfigProperties(name = "warp.json")
public final class WarpConfig extends FlameConfig {

  private Map<String, Warp> warpMap = new HashMap<>(Map.of("skrzynki",
      new Warp("skrzynki", "&6&lSKRZYNIE PREMIUM", Material.CHEST, 23,
          new Location(Bukkit.getWorld("world"), 0, 100, 0))));

  public Map<String, Warp> getWarpMap() {
    return warpMap;
  }


}

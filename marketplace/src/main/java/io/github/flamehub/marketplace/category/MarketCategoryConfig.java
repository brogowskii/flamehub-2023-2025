package io.github.flamehub.marketplace.category;

import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;

@FlameConfigProperties(name = "categories.json")
public final class MarketCategoryConfig extends FlameConfig {

  private final List<MarketCategoryWrapper> marketCategoryWrappers = List.of(
      new MarketCategoryWrapper("all", "&a&lWszystko", 45, Material.NETHER_STAR, new ArrayList<>()),
      new MarketCategoryWrapper("tools", "&3&lNarzędzia", 46, Material.NETHERITE_SWORD,
          Arrays.asList(
              new MarketCategory("swords", "Miecze", List.of("DIAMOND_SWORD", "NETHERITE_SWORD"), null, 0),
              new MarketCategory("shovels", "Łopaty", List.of("DIAMOND_SHOVEL", "NETHERITE_SHOVEL"), null, 0)
          ))
  );

  public MarketCategoryConfig() {
  }

  public List<MarketCategoryWrapper> getMarketCategoryWrappers() {
    return marketCategoryWrappers;
  }

}

package io.github.flamehub.auctionhouse.bukkit.category;

import io.github.flamehub.auctionhouse.commons.category.AuctionHouseCategory;
import io.github.flamehub.auctionhouse.commons.category.AuctionHouseCategoryIcon;
import io.github.flamehub.commons.config.EnableRemote;
import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import java.util.List;

@FlameConfigProperties(name = "auctionhouseCategories.json")
@EnableRemote(collection = "configs")
public final class AuctionHouseCategoryConfig extends FlameConfig {

  private List<AuctionHouseCategory> auctionHouseCategories = List.of(
      new AuctionHouseCategory("swords", "Miecze",
          new AuctionHouseCategoryIcon("NETHERITE_SWORD", "&7Kategoria: &fMiecze", List.of(), 20),
          List.of("DIAMOND_SWORD", "NETHERITE_SWORD"))
  );

  public List<AuctionHouseCategory> getAuctionHouseCategories() {
    return auctionHouseCategories;
  }

}

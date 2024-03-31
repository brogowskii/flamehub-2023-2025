package io.github.flamehub.auctionhouse.bukkit.category;

import eu.okaeri.configs.OkaeriConfig;
import io.github.flamehub.auctionhouse.commons.category.AuctionHouseCategory;
import io.github.flamehub.auctionhouse.commons.category.AuctionHouseCategoryIcon;

import java.util.List;

public final class AuctionHouseCategoryConfig extends OkaeriConfig {

    private List<AuctionHouseCategory> auctionHouseCategories = List.of(
            new AuctionHouseCategory("swords", "Miecze", new AuctionHouseCategoryIcon("NETHERITE_SWORD", "&7Kategoria: &fMiecze", List.of(), 20), List.of("DIAMOND_SWORD", "NETHERITE_SWORD"))
    );

    public List<AuctionHouseCategory> getAuctionHouseCategories() {
        return auctionHouseCategories;
    }

}

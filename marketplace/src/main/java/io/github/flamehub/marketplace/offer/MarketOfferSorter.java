package io.github.flamehub.marketplace.offer;

import io.github.flamehub.marketplace.category.MarketCategory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.enchantments.Enchantment;

public final class MarketOfferSorter {

  public List<MarketOffer> sorted(
      final MarketCategory category,
      final MarketOfferSort sortType,
      final Collection<MarketOffer> marketOffers,
      final MarketOfferFilter filter,
      final double economyBalance) {

    List<MarketOffer> marketOffersSorted = new ArrayList<>(marketOffers);
    if (category != null) {
      final List<String> materials = category.getMaterials();
      marketOffersSorted = marketOffers.stream()
          .filter(offer -> materials.contains(offer.getItem().getItemStack().getType().toString()))
          .collect(Collectors.toList());
    }

    if (filter == MarketOfferFilter.ENOUGH_MONEY) {
      marketOffersSorted = marketOffers.stream()
          .filter(offer -> offer.getPrice().doubleValue() <= economyBalance)
          .collect(Collectors.toList());
    }

    switch (sortType) {

      case ASCENDING_PRICE -> marketOffersSorted.sort(
          Comparator.comparingDouble(o -> o.getPrice().doubleValue()));

      case DESCENDING_PRICE -> marketOffersSorted.sort(
          (o1, o2) -> Double.compare(o2.getPrice().doubleValue(), o1.getPrice().doubleValue()));

      case NEWEST -> marketOffersSorted.sort((o1, o2) ->
          Long.compare(
              o2.getCreationDate().toInstant().toEpochMilli(),
              o1.getCreationDate().toInstant().toEpochMilli())
      );

      case LEVEL -> marketOffersSorted.sort(
          Comparator.comparingInt((MarketOffer o) -> o.getItem().getItemStack()
                  .getEnchantmentLevel(Enchantment.DURABILITY))
              .reversed()
      );

      case OLDEST -> marketOffersSorted.sort(
          Comparator.comparingLong(o -> o.getCreationDate().toInstant().toEpochMilli())
      );

      case NONE -> {
      }

    }

    return marketOffersSorted;

  }

}

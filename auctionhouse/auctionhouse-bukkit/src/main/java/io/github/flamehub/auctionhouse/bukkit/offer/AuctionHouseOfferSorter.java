package io.github.flamehub.auctionhouse.bukkit.offer;

import io.github.flamehub.auctionhouse.commons.category.AuctionHouseCategory;
import io.github.flamehub.auctionhouse.commons.offer.AuctionHouseOfferSort;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class AuctionHouseOfferSorter {

  public List<AuctionHouseOfferDto> sorted(AuctionHouseCategory category,
      AuctionHouseOfferSort sortType, Collection<AuctionHouseOfferDto> auctionHouseOffers) {
    List<AuctionHouseOfferDto> auctionHouseOffersSorted = new ArrayList<>(auctionHouseOffers);

    if (category != null) {
      List<String> materials = category.getMaterials();
      auctionHouseOffersSorted = auctionHouseOffers.stream()
          .filter(offer -> materials.contains(offer.getItem().getMaterial()))
          .collect(Collectors.toList());
    }

    switch (sortType) {

      case ASCENDING_PRICE -> auctionHouseOffersSorted.sort(
          Comparator.comparingDouble(o -> o.getPrice().doubleValue()));

      case DESCENDING_PRICE -> auctionHouseOffersSorted.sort(
          (o1, o2) -> Double.compare(o2.getPrice().doubleValue(), o1.getPrice().doubleValue()));

      case NEWEST -> auctionHouseOffersSorted.sort((o1, o2) ->
          Long.compare(
              o2.getCreationDate().toInstant().toEpochMilli(),
              o1.getCreationDate().toInstant().toEpochMilli())
      );

      case OLDEST -> auctionHouseOffersSorted.sort((o1, o2) ->
          Long.compare(
              o1.getCreationDate().toInstant().toEpochMilli(),
              o2.getCreationDate().toInstant().toEpochMilli())
      );

      case NONE -> {
      }

    }

    return auctionHouseOffersSorted;

  }

}

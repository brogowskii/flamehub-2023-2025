package io.github.flamehub.marketplace.offer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MarketOfferPageSorter {

  public Map<Integer, List<MarketOffer>> offersByPageMap(
     final Collection<MarketOffer> offerWrappers) {

    final int itemsPerPage = 28;
    final List<MarketOffer> allOffers = new ArrayList<>(offerWrappers);
    final int totalPages = (int) Math.ceil((double) allOffers.size() / itemsPerPage);

    final Map<Integer, List<MarketOffer>> pages = new HashMap<>();
    for (int i = 0; i < totalPages; i++) {
      final int startIndex = i * itemsPerPage;
      final int endIndex = Math.min(startIndex + itemsPerPage, allOffers.size());
      final List<MarketOffer> pageOffers = allOffers.subList(startIndex, endIndex);
      pages.put(i + 1, pageOffers);
    }

    return pages;
  }

}

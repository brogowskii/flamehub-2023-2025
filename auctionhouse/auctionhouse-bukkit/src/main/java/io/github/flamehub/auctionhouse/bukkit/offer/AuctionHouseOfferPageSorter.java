package io.github.flamehub.auctionhouse.bukkit.offer;

import java.util.*;

public final class AuctionHouseOfferPageSorter {

    public Map<Integer, List<AuctionHouseOfferDto>> offersByPageMap(Collection<AuctionHouseOfferDto> offerWrappers) {

        int itemsPerPage = 28;
        List<AuctionHouseOfferDto> allOffers = new ArrayList<>(offerWrappers);
        int totalPages = (int) Math.ceil((double) allOffers.size() / itemsPerPage);

        Map<Integer, List<AuctionHouseOfferDto>> pages = new HashMap<>();
        for (int i = 0; i < totalPages; i++) {
            int startIndex = i * itemsPerPage;
            int endIndex = Math.min(startIndex + itemsPerPage, allOffers.size());
            List<AuctionHouseOfferDto> pageOffers = allOffers.subList(startIndex, endIndex);
            pages.put(i + 1, pageOffers);
        }


        return pages;
    }

}

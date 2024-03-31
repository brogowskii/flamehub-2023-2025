package io.github.flamehub.auctionhouse.commons.page;

import io.github.flamehub.auctionhouse.commons.offer.AuctionHouseOffer;

import java.util.*;

public final class AuctionHouseOfferPageSorter {

    public Map<Integer, List<AuctionHouseOffer>> offersByPageMap(Collection<AuctionHouseOffer> offerWrappers) {

        int itemsPerPage = 28;
        List<AuctionHouseOffer> allOffers = new ArrayList<>(offerWrappers);
        int totalPages = (int) Math.ceil((double) allOffers.size() / itemsPerPage);

        Map<Integer, List<AuctionHouseOffer>> pages = new HashMap<>();
        for (int i = 0; i < totalPages; i++) {
            int startIndex = i * itemsPerPage;
            int endIndex = Math.min(startIndex + itemsPerPage, allOffers.size());
            List<AuctionHouseOffer> pageOffers = allOffers.subList(startIndex, endIndex);
            pages.put(i + 1, pageOffers);
        }


        return pages;
    }

}

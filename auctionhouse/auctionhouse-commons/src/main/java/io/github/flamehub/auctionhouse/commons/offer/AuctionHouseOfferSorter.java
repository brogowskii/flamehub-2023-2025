package io.github.flamehub.auctionhouse.commons.offer;

import io.github.flamehub.auctionhouse.commons.category.AuctionHouseCategory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class AuctionHouseOfferSorter {

    public List<AuctionHouseOffer> sorted(AuctionHouseCategory category, AuctionHouseOfferSort sortType, Collection<AuctionHouseOffer> auctionHouseOffers) {
        List<AuctionHouseOffer> auctionHouseOffersSorted = new ArrayList<>(auctionHouseOffers);

        if (category != null) {
            List<String> materials = category.getMaterials();
            auctionHouseOffersSorted = auctionHouseOffers.stream()
                    .filter(offer -> materials.contains(offer.getItem().getMaterial()))
                    .collect(Collectors.toList());
        }

        switch (sortType) {

            case ASCENDING_PRICE -> auctionHouseOffersSorted.sort(Comparator.comparingDouble(o -> o.getPrice().doubleValue()));

            case DESCENDING_PRICE -> auctionHouseOffersSorted.sort((o1, o2) -> Double.compare(o2.getPrice().doubleValue(), o1.getPrice().doubleValue()));

            case NONE -> {
            }

        }

        return auctionHouseOffersSorted;

    }

}

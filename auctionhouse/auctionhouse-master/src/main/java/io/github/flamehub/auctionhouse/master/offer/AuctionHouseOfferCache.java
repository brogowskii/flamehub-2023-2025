package io.github.flamehub.auctionhouse.master.offer;

import io.github.flamehub.auctionhouse.commons.offer.AuctionHouseOffer;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class AuctionHouseOfferCache {

    private final Map<UUID, AuctionHouseOffer> auctionHouseOfferMap = new ConcurrentHashMap<>();

    public AuctionHouseOffer findById(UUID uniqueId) {
        return this.auctionHouseOfferMap.get(uniqueId);
    }

    public void add(AuctionHouseOffer offer) {
        this.auctionHouseOfferMap.put(offer.getOfferId(), offer);
    }

    public void remove(UUID uniqueId) {
        this.auctionHouseOfferMap.remove(uniqueId);
    }

    public Collection<AuctionHouseOffer> getValues() {
        return this.auctionHouseOfferMap.values();
    }


}

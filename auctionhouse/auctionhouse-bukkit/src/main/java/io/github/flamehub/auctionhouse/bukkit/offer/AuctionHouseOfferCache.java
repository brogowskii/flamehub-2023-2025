package io.github.flamehub.auctionhouse.bukkit.offer;

import io.github.flamehub.commons.cache.KeyValueCache;

import java.util.UUID;

public final class AuctionHouseOfferCache extends KeyValueCache<UUID, AuctionHouseOfferDto> {
    public AuctionHouseOfferCache() {
        super(true);
    }



}

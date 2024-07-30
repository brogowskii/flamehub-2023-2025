package io.github.flamehub.auctionhouse.master;

import io.github.flamehub.auctionhouse.commons.offer.AuctionHouseOffer;
import io.github.flamehub.commons.cache.KeyValueCache;
import java.util.UUID;

public final class AuctionHouseOfferCache extends KeyValueCache<UUID, AuctionHouseOffer> {

  public AuctionHouseOfferCache() {
    super(true);
  }

}

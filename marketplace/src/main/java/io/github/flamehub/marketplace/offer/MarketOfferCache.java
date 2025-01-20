package io.github.flamehub.marketplace.offer;

import io.github.flamehub.commons.cache.KeyValueCache;
import java.util.UUID;

public final class MarketOfferCache extends KeyValueCache<UUID, MarketOffer> {

  public MarketOfferCache() {
    super(true);
  }


}

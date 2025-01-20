package io.github.flamehub.marketplace.offer;

import dev.morphia.Datastore;
import io.github.flamehub.commons.database.DatabaseRepository;

public final class MarketOfferRepository extends DatabaseRepository<MarketOffer> {

  public MarketOfferRepository(final Datastore datastore) {
    super(datastore, MarketOffer.class);
  }
}

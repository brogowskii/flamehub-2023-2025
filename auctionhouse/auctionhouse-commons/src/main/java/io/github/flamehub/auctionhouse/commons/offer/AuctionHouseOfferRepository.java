package io.github.flamehub.auctionhouse.commons.offer;

import dev.morphia.Datastore;
import io.github.flamehub.commons.database.DatabaseRepository;

public final class AuctionHouseOfferRepository extends DatabaseRepository<AuctionHouseOffer> {

  public AuctionHouseOfferRepository(Datastore datastore, Class<AuctionHouseOffer> entityClass) {
    super(datastore, entityClass);
  }
}

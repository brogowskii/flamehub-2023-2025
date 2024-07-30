package io.github.flamehub.auctionhouse.master;

import io.github.flamehub.auctionhouse.commons.AuctionHouseConfig;
import io.github.flamehub.auctionhouse.commons.offer.AuctionHouseOffer;
import io.github.flamehub.auctionhouse.commons.offer.AuctionHouseOfferRepository;
import io.github.flamehub.commons.config.FlameConfigService;
import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.messenger.RedisMessenger;
import java.io.File;
import java.net.URISyntaxException;

public class AuctionHouseMaster {

  private final RedisMessenger redisMessenger;
  private final DatabaseConnector databaseConnector;
  private final FlameConfigService flameConfigService;

  private final AuctionHouseConfig auctionHouseConfig;
  private final AuctionHouseOfferCache auctionHouseOfferCache;
  private final AuctionHouseOfferRepository auctionHouseOfferRepository;

  public AuctionHouseMaster(RedisMessenger redisMessenger, DatabaseConnector databaseConnector,
      FlameConfigService flameConfigService, File file) throws URISyntaxException {
    this.redisMessenger = redisMessenger;
    this.databaseConnector = databaseConnector;
    this.flameConfigService = flameConfigService;

    this.auctionHouseConfig = this.flameConfigService.getOrCreate(file, AuctionHouseConfig.class);

    this.auctionHouseOfferCache = new AuctionHouseOfferCache();
    this.auctionHouseOfferRepository = new AuctionHouseOfferRepository(
        DatastoreFactory.create(
            this.databaseConnector.getMongoClient(),
            this.auctionHouseConfig.getDatabase(),
            AuctionHouseOffer.class
        ),
        AuctionHouseOffer.class
    );

    this.auctionHouseOfferRepository.loadAll()
        .forEach(
            auctionHouseOffer -> this.auctionHouseOfferCache.add(auctionHouseOffer.getOfferId(),
                auctionHouseOffer));

    this.redisMessenger.subscribe(
        this.auctionHouseConfig.getMasterChannel(),
        new AuctionHouseHandler(
            this.redisMessenger,
            this.auctionHouseConfig,
            this.auctionHouseOfferCache,
            this.auctionHouseOfferRepository
        )
    );
  }

}
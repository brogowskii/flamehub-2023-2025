package io.github.flamehub.auctionhouse.master;

import dev.morphia.Datastore;
import dev.morphia.Morphia;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.json.gson.JsonGsonConfigurer;
import io.github.flamehub.auctionhouse.commons.AuctionHouseConfig;
import io.github.flamehub.auctionhouse.commons.offer.AuctionHouseOffer;
import io.github.flamehub.auctionhouse.commons.offer.AuctionHouseOfferRepository;
import io.github.flamehub.commons.database.DatabaseConfig;
import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.redis.RedisConfig;
import io.github.flamehub.commons.redis.RedisService;

import javax.xml.crypto.Data;

public class AuctionHouseMaster {

    private final RedisConfig redisConfig;
    private final RedisMessenger redisMessenger;
    private final RedisService redisService;

    private final DatabaseConfig databaseConfig;
    private final DatabaseConnector databaseConnector;

    private final AuctionHouseConfig auctionHouseConfig;

    private final AuctionHouseOfferCache auctionHouseOfferCache;
    private final AuctionHouseOfferRepository auctionHouseOfferRepository;

    public AuctionHouseMaster() {

        this.redisConfig = ConfigManager.create(RedisConfig.class, (it) -> {
            it.withConfigurer(new JsonGsonConfigurer());
            it.withBindFile( "redis.json");
            it.saveDefaults();
            it.load(true);
        });

        this.redisService = new RedisService(this.redisConfig.getHost(), this.redisConfig.getPassword(), this.redisConfig.getPort());
        this.redisMessenger = new RedisMessenger(this.redisService.getClient());

        this.databaseConfig = ConfigManager.create(DatabaseConfig.class, (it) -> {
            it.withConfigurer(new JsonGsonConfigurer());
            it.withBindFile( "database.json");
            it.saveDefaults();
            it.load(true);
        });

        this.databaseConnector = new DatabaseConnector(this.databaseConfig.getMongoUri());

        this.auctionHouseConfig = ConfigManager.create(AuctionHouseConfig.class, (it) -> {
            it.withConfigurer(new JsonGsonConfigurer());
            it.withBindFile( "auctionHouse.json");
            it.saveDefaults();
            it.load(true);
        });

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
                .forEach(auctionHouseOffer -> this.auctionHouseOfferCache.add(auctionHouseOffer.getOfferId(), auctionHouseOffer));


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
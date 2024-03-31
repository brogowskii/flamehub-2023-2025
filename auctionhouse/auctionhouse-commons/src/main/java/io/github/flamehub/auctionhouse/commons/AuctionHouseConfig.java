package io.github.flamehub.auctionhouse.commons;

import eu.okaeri.configs.OkaeriConfig;

public final class AuctionHouseConfig extends OkaeriConfig {

    private String redisChannel = "boxpvp_auction_house";
    private String database = "boxpvp";

    public String getRedisChannel() {
        return redisChannel;
    }

    public String getDatabase() {
        return database;
    }

}

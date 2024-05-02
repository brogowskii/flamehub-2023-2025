package io.github.flamehub.auctionhouse.commons;

import eu.okaeri.configs.OkaeriConfig;

public final class AuctionHouseConfig extends OkaeriConfig {

    private String slavesUpdateChannel = "skypvp_auctionhouse_slaves_update";
    private String masterChannel = "skypvp_auctionhouse_master";
    private String database = "skypvp";

    public String getMasterChannel() {
        return masterChannel;
    }

    public String getDatabase() {
        return database;
    }

    public String getSlavesUpdateChannel() {
        return slavesUpdateChannel;
    }
}

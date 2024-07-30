package io.github.flamehub.auctionhouse.commons;


import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;

@FlameConfigProperties(name = "auctionhouse.json")
public final class AuctionHouseConfig extends FlameConfig {

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

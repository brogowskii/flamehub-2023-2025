package io.github.flamehub.auctionhouse.commons.action;

import io.github.flamehub.commons.messenger.packet.Packet;

public final class AuctionHouseOfferAdd implements Packet {

  private final String offerJson;

  public AuctionHouseOfferAdd(String offerJson) {
    this.offerJson = offerJson;
  }

  public String getOfferJson() {
    return offerJson;
  }
}

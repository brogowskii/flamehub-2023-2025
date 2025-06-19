package io.github.flamehub.marketplace.offer;

import io.github.flamehub.commons.messenger.packet.Packet;

public final class MarketOfferAdd implements Packet {

  private MarketOffer offer;

  public MarketOfferAdd(final MarketOffer offer) {
    this.offer = offer;
  }

  public MarketOffer getOffer() {
    return offer;
  }
}

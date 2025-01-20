package io.github.flamehub.marketplace.offer;

import io.github.flamehub.commons.messenger.packet.Packet;
import java.util.UUID;

public final class MarketOfferRemove implements Packet {

  private final UUID offerId;

  public MarketOfferRemove(final UUID offerId) {
    this.offerId = offerId;
  }

  public UUID getOfferId() {
    return offerId;
  }
}

package io.github.flamehub.marketplace;

import io.github.flamehub.marketplace.offer.MarketOffer;
import io.github.flamehub.marketplace.offer.MarketOfferAdd;
import io.github.flamehub.marketplace.offer.MarketOfferCache;
import io.github.flamehub.marketplace.offer.MarketOfferRemove;
import io.github.flamehub.commons.messenger.packet.PacketHandler;
import java.io.IOException;

public final class MarketHandler {

  private final MarketOfferCache marketOfferCache;

  public MarketHandler(MarketOfferCache marketOfferCache) {
    this.marketOfferCache = marketOfferCache;
  }

  @PacketHandler
  public void handleOfferAdd(MarketOfferAdd packet) {
    final MarketOffer offer = packet.getOffer();
    try {
      offer.getItem().postLoad();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    marketOfferCache.add(offer.getOfferId(), offer);
  }

  @PacketHandler
  public void handleOfferRemove(MarketOfferRemove packet) {
    marketOfferCache.remove(packet.getOfferId());
  }

}

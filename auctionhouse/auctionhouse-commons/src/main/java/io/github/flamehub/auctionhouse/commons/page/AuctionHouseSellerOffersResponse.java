package io.github.flamehub.auctionhouse.commons.page;

import io.github.flamehub.commons.messenger.packet.PacketResponse;
import java.util.UUID;

public final class AuctionHouseSellerOffersResponse extends PacketResponse {

  private final AuctionHouseOfferPage auctionHouseOfferPage;

  public AuctionHouseSellerOffersResponse(UUID uniqueId,
      AuctionHouseOfferPage auctionHouseOfferPage) {
    super(uniqueId);
    this.auctionHouseOfferPage = auctionHouseOfferPage;
  }

  public AuctionHouseOfferPage getAuctionHouseOfferPage() {
    return auctionHouseOfferPage;
  }
}

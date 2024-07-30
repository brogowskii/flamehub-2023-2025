package io.github.flamehub.auctionhouse.commons.action;

import io.github.flamehub.commons.messenger.packet.PacketResponse;
import java.util.UUID;

public class AuctionHouseOfferRemoveResponse extends PacketResponse {

  private final boolean success;

  public AuctionHouseOfferRemoveResponse(UUID uniqueId, boolean success) {
    super(uniqueId);
    this.success = success;
  }

  public boolean isSuccess() {
    return success;
  }
}

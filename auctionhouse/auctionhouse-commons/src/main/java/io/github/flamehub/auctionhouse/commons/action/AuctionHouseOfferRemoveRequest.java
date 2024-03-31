package io.github.flamehub.auctionhouse.commons.action;

import io.github.flamehub.commons.messenger.packet.PacketRequest;

import java.util.UUID;

public class AuctionHouseOfferRemoveRequest extends PacketRequest {

    private final UUID offerId;

    public AuctionHouseOfferRemoveRequest(UUID offerId) {
        this.offerId = offerId;
    }

    public UUID getOfferId() {
        return offerId;
    }

}

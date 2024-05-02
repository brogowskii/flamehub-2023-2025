package io.github.flamehub.auctionhouse.commons.action;

import io.github.flamehub.commons.messenger.packet.Packet;

import java.util.UUID;

public final class AuctionHouseOfferRemove implements Packet {

    private final UUID offerId;

    public AuctionHouseOfferRemove(UUID offerId) {
        this.offerId = offerId;
    }

    public UUID getOfferId() {
        return offerId;
    }
}

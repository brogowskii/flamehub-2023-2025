package io.github.flamehub.auctionhouse.commons.page;

import io.github.flamehub.commons.messenger.packet.PacketRequest;

import java.util.UUID;

public final class AuctionHouseSellerOffersRequest extends PacketRequest {

    private final UUID sellerUniqueId;

    public AuctionHouseSellerOffersRequest(UUID sellerUniqueId) {
        this.sellerUniqueId = sellerUniqueId;
    }

    public UUID getSellerUniqueId() {
        return sellerUniqueId;
    }
}

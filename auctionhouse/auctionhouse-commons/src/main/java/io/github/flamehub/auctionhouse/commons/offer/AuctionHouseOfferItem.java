package io.github.flamehub.auctionhouse.commons.offer;

import dev.morphia.annotations.Entity;

@Entity
public final class AuctionHouseOfferItem {

    private String serializedItemStack;
    private String material;

    public AuctionHouseOfferItem() {

    }

    public AuctionHouseOfferItem(String serializedItemStack, String material) {
        this.serializedItemStack = serializedItemStack;
        this.material = material;
    }

    public String getSerializedItemStack() {
        return serializedItemStack;
    }

    public String getMaterial() {
        return material;
    }
}

package io.github.flamehub.auctionhouse.commons;

import dev.morphia.annotations.Entity;

import java.util.UUID;

@Entity
public final class AuctionHouseSeller {

    private UUID uniqueId;
    private String name;

    public AuctionHouseSeller() {

    }

    public AuctionHouseSeller(UUID uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getName() {
        return name;
    }
}

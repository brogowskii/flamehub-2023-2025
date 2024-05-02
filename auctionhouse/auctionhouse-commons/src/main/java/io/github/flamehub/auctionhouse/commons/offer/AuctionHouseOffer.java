package io.github.flamehub.auctionhouse.commons.offer;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import io.github.flamehub.auctionhouse.commons.AuctionHouseSeller;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Entity("aucionhouse_offers")
public class AuctionHouseOffer {

    @Id
    private UUID offerId;
    private AuctionHouseSeller seller;

    private AuctionHouseOfferItem item;
    private BigDecimal price;
    private Date creationDate;
    private Instant expirationTime;

    public AuctionHouseOffer() {
    }

    public AuctionHouseOffer(UUID offerId, AuctionHouseSeller seller, AuctionHouseOfferItem item, BigDecimal price, Instant expirationTime) {
        this.offerId = offerId;
        this.seller = seller;
        this.item = item;
        this.price = price;
        this.expirationTime = expirationTime;
        this.creationDate = new Date();
    }

    public UUID getOfferId() {
        return offerId;
    }

    public AuctionHouseSeller getSeller() {
        return seller;
    }

    public AuctionHouseOfferItem getItem() {
        return item;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Instant getExpirationTime() {
        return expirationTime;
    }

    public Date getCreationDate() {
        return creationDate;
    }
}

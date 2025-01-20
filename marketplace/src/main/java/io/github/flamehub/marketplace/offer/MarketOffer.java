package io.github.flamehub.marketplace.offer;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import io.github.flamehub.marketplace.MarketSeller;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Entity("market_offers")
public final class MarketOffer {

  @Id
  private UUID offerId;
  private MarketSeller seller;

  private MarketOfferItem item;
  private BigDecimal price;
  private Date creationDate;
  private Instant expirationTime;

  public MarketOffer() {
  }

  public MarketOffer(
      final UUID offerId,
      final MarketSeller seller,
      final MarketOfferItem item,
      final BigDecimal price,
      final Instant expirationTime) {
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

  public MarketSeller getSeller() {
    return seller;
  }

  public MarketOfferItem getItem() {
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

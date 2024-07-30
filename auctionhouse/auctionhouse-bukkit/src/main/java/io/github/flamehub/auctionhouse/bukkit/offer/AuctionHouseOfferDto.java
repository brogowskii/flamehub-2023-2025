package io.github.flamehub.auctionhouse.bukkit.offer;

import io.github.flamehub.auctionhouse.commons.AuctionHouseSeller;
import io.github.flamehub.auctionhouse.commons.offer.AuctionHouseOffer;
import io.github.flamehub.auctionhouse.commons.offer.AuctionHouseOfferItem;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;

public final class AuctionHouseOfferDto extends AuctionHouseOffer {

  private ItemStack itemStack;

  public AuctionHouseOfferDto() {
  }

  public AuctionHouseOfferDto(UUID offerId, AuctionHouseSeller seller, AuctionHouseOfferItem item,
      BigDecimal price, Instant expirationTime) {
    super(offerId, seller, item, price, expirationTime);
  }

  public ItemStack getItemStack() {
    return itemStack;
  }

  public void setItemStack(ItemStack itemStack) {
    this.itemStack = itemStack;
  }
}

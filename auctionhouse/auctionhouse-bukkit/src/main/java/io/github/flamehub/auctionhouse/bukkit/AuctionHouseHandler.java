package io.github.flamehub.auctionhouse.bukkit;

import io.github.flamehub.auctionhouse.bukkit.offer.AuctionHouseOfferCache;
import io.github.flamehub.auctionhouse.bukkit.offer.AuctionHouseOfferDto;
import io.github.flamehub.auctionhouse.commons.AuctionHouseJsonUtil;
import io.github.flamehub.auctionhouse.commons.action.AuctionHouseOfferAdd;
import io.github.flamehub.auctionhouse.commons.action.AuctionHouseOfferRemove;
import io.github.flamehub.auctionhouse.commons.offer.AuctionHouseOffer;
import io.github.flamehub.commons.messenger.packet.PacketHandler;
import java.io.IOException;
import org.bukkit.inventory.ItemStack;

public final class AuctionHouseHandler {

  private final AuctionHouseOfferCache auctionHouseOfferCache;

  public AuctionHouseHandler(AuctionHouseOfferCache auctionHouseOfferCache) {
    this.auctionHouseOfferCache = auctionHouseOfferCache;
  }

  @PacketHandler
  public void handleOfferAdd(AuctionHouseOfferAdd packet) {
    AuctionHouseOffer auctionHouseOffer = AuctionHouseJsonUtil.GSON.fromJson(packet.getOfferJson(),
        AuctionHouseOffer.class);
    AuctionHouseOfferDto auctionHouseOfferDto = new AuctionHouseOfferDto(
        auctionHouseOffer.getOfferId(),
        auctionHouseOffer.getSeller(),
        auctionHouseOffer.getItem(),
        auctionHouseOffer.getPrice(),
        auctionHouseOffer.getExpirationTime()
    );

    ItemStack itemStack;
    try {
      itemStack = AuctionHouseSerializer.deserialize(
          auctionHouseOffer.getItem().getSerializedItemStack());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    auctionHouseOfferDto.setItemStack(itemStack);
    this.auctionHouseOfferCache.add(auctionHouseOffer.getOfferId(), auctionHouseOfferDto);
  }

  @PacketHandler
  public void handleOfferRemove(AuctionHouseOfferRemove packet) {
    this.auctionHouseOfferCache.remove(packet.getOfferId());
  }

}

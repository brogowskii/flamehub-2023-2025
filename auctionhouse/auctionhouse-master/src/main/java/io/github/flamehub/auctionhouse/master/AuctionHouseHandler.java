package io.github.flamehub.auctionhouse.master;

import io.github.flamehub.auctionhouse.commons.AuctionHouseConfig;
import io.github.flamehub.auctionhouse.commons.AuctionHouseJsonUtil;
import io.github.flamehub.auctionhouse.commons.action.AuctionHouseOfferAdd;
import io.github.flamehub.auctionhouse.commons.action.AuctionHouseOfferRemove;
import io.github.flamehub.auctionhouse.commons.action.AuctionHouseOfferRemoveRequest;
import io.github.flamehub.auctionhouse.commons.action.AuctionHouseOfferRemoveResponse;
import io.github.flamehub.auctionhouse.commons.offer.AuctionHouseOffer;
import io.github.flamehub.auctionhouse.commons.offer.AuctionHouseOfferRepository;
import io.github.flamehub.auctionhouse.commons.page.AuctionHouseOfferPage;
import io.github.flamehub.auctionhouse.commons.page.AuctionHouseSellerOffersRequest;
import io.github.flamehub.auctionhouse.commons.page.AuctionHouseSellerOffersResponse;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.messenger.packet.PacketHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class AuctionHouseHandler {

  private final RedisMessenger redisMessenger;

  private final AuctionHouseConfig auctionHouseConfig;
  private final AuctionHouseOfferCache auctionHouseOfferCache;
  private final AuctionHouseOfferRepository auctionHouseOfferRepository;

  public AuctionHouseHandler(
      RedisMessenger redisMessenger,
      AuctionHouseConfig auctionHouseConfig,
      AuctionHouseOfferCache auctionHouseOfferCache,
      AuctionHouseOfferRepository auctionHouseOfferRepository
  ) {
    this.redisMessenger = redisMessenger;
    this.auctionHouseConfig = auctionHouseConfig;
    this.auctionHouseOfferCache = auctionHouseOfferCache;
    this.auctionHouseOfferRepository = auctionHouseOfferRepository;
  }

  @PacketHandler
  public void add(AuctionHouseOfferAdd packet) {
    AuctionHouseOffer offer = AuctionHouseJsonUtil.GSON.fromJson(packet.getOfferJson(),
        AuctionHouseOffer.class);
    this.auctionHouseOfferCache.add(offer.getOfferId(), offer);
    this.auctionHouseOfferRepository.save(offer);
    this.redisMessenger.publish(this.auctionHouseConfig.getSlavesUpdateChannel(),
        new AuctionHouseOfferAdd(packet.getOfferJson()));

  }

  @PacketHandler
  public void buyRequest(AuctionHouseOfferRemoveRequest request) {
    UUID offerId = request.getOfferId();
    boolean success;

    AuctionHouseOffer auctionHouseOffer = this.auctionHouseOfferCache.findByKey(offerId);
    if (auctionHouseOffer == null) {
      success = false;
    } else {
      success = true;

      this.auctionHouseOfferCache.remove(offerId);
      this.auctionHouseOfferRepository.delete(auctionHouseOffer);
      this.redisMessenger.publish(this.auctionHouseConfig.getSlavesUpdateChannel(),
          new AuctionHouseOfferRemove(auctionHouseOffer.getOfferId()));
    }

    AuctionHouseOfferRemoveResponse response = new AuctionHouseOfferRemoveResponse(
        request.getUniqueId(), success);
    this.redisMessenger.publish("callbacks", response);

  }

  @PacketHandler
  public void sellerItems(AuctionHouseSellerOffersRequest request) {
    UUID sellerUniqueId = request.getSellerUniqueId();
    List<AuctionHouseOffer> values = new ArrayList<>(this.auctionHouseOfferCache.values());
    List<String> serializedOffers = values.stream()
        .filter(offer -> offer.getSeller().getUniqueId().equals(sellerUniqueId))
        .map(AuctionHouseJsonUtil.GSON::toJson)
        .toList();

    AuctionHouseOfferPage auctionHouseOfferPage = new AuctionHouseOfferPage(serializedOffers, 1);
    AuctionHouseSellerOffersResponse response = new AuctionHouseSellerOffersResponse(
        request.getUniqueId(), auctionHouseOfferPage);
    this.redisMessenger.publish("callbacks", response);

  }

}

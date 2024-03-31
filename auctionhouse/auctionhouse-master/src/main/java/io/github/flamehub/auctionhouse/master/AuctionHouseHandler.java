package io.github.flamehub.auctionhouse.master;

import io.github.flamehub.auctionhouse.commons.AuctionHouseJsonUtil;
import io.github.flamehub.auctionhouse.commons.action.AuctionHouseOfferAdd;
import io.github.flamehub.auctionhouse.commons.action.AuctionHouseOfferRemoveRequest;
import io.github.flamehub.auctionhouse.commons.action.AuctionHouseOfferRemoveResponse;
import io.github.flamehub.auctionhouse.commons.offer.AuctionHouseOffer;
import io.github.flamehub.auctionhouse.commons.offer.AuctionHouseOfferSorter;
import io.github.flamehub.auctionhouse.commons.page.*;
import io.github.flamehub.auctionhouse.master.offer.AuctionHouseOfferCache;
import io.github.flamehub.auctionhouse.master.offer.AuctionHouseOfferRepository;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.messenger.packet.PacketHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public final class AuctionHouseHandler {

    private final RedisMessenger redisMessenger;

    private final AuctionHouseOfferCache auctionHouseOfferCache;
    private final AuctionHouseOfferSorter auctionHouseOfferSorter;
    private final AuctionHouseOfferPageSorter auctionHouseOfferPageSorter;
    private final AuctionHouseOfferRepository auctionHouseOfferRepository;

    public AuctionHouseHandler(
            RedisMessenger redisMessenger,
            AuctionHouseOfferCache auctionHouseOfferCache,
            AuctionHouseOfferSorter auctionHouseOfferSorter,
            AuctionHouseOfferPageSorter auctionHouseOfferPageSorter,
            AuctionHouseOfferRepository auctionHouseOfferRepository
    ) {
        this.redisMessenger = redisMessenger;
        this.auctionHouseOfferCache = auctionHouseOfferCache;
        this.auctionHouseOfferSorter = auctionHouseOfferSorter;
        this.auctionHouseOfferPageSorter = auctionHouseOfferPageSorter;
        this.auctionHouseOfferRepository = auctionHouseOfferRepository;
    }

    @PacketHandler
    public void handle(AuctionHouseOfferPageRequest request) {
        int pageId = request.getPageId();
        List<AuctionHouseOffer> values = new ArrayList<>(this.auctionHouseOfferCache.getValues());
        values = values.stream()
                .filter(offer -> offer.getExpirationTime().toEpochMilli() > System.currentTimeMillis())
                .toList();
        values = this.auctionHouseOfferSorter.sorted(request.getCategory(), request.getSort(), values);

        Map<Integer, List<AuctionHouseOffer>> offersByPageMap = this.auctionHouseOfferPageSorter.offersByPageMap(values);
        List<AuctionHouseOffer> auctionHouseOffers = offersByPageMap.get(pageId);
        if (auctionHouseOffers == null) {
            auctionHouseOffers = new ArrayList<>();
        }

        int maxPage = (int) Math.ceil((double) values.size() / 28);
        AuctionHouseOfferPage auctionHouseOfferPage = new AuctionHouseOfferPage(auctionHouseOffers.stream()
                .map(AuctionHouseJsonUtil.GSON::toJson)
                .collect(Collectors.toList()), maxPage);

        AuctionHouseOfferPageResponse auctionHouseOfferPageResponse = new AuctionHouseOfferPageResponse(request.getUniqueId(), auctionHouseOfferPage);
        System.out.println("received request to to get page: " + request.getPageId() +  " : " + request.getUniqueId());
        this.redisMessenger.publish("callbacks", auctionHouseOfferPageResponse);

    }

    @PacketHandler
    public void add(AuctionHouseOfferAdd packet) {
        AuctionHouseOffer offer = AuctionHouseJsonUtil.GSON.fromJson(packet.getOfferJson(), AuctionHouseOffer.class);
        this.auctionHouseOfferCache.add(offer);
        this.auctionHouseOfferRepository.save(offer);
        System.out.println("added " + packet.getOfferJson());
    }

    @PacketHandler
    public void buyRequest(AuctionHouseOfferRemoveRequest request) {
        UUID offerId = request.getOfferId();
        boolean success;

        AuctionHouseOffer auctionHouseOffer = this.auctionHouseOfferCache.findById(offerId);
        if (auctionHouseOffer == null) {
            success = false;
        }
        else {
            success = true;

            this.auctionHouseOfferCache.remove(offerId);
            this.auctionHouseOfferRepository.delete(auctionHouseOffer);
            System.out.println("removed " + request.getOfferId());
        }

        AuctionHouseOfferRemoveResponse response = new AuctionHouseOfferRemoveResponse(request.getUniqueId(), success);
        this.redisMessenger.publish("callbacks", response);

    }

    @PacketHandler
    public void sellerItems(AuctionHouseSellerOffersRequest request) {
        UUID sellerUniqueId = request.getSellerUniqueId();
        List<AuctionHouseOffer> values = new ArrayList<>(this.auctionHouseOfferCache.getValues());
        List<String> serializedOffers = values.stream()
                .filter(offer -> offer.getSeller().getUniqueId().equals(sellerUniqueId))
                .map(AuctionHouseJsonUtil.GSON::toJson)
                .toList();

        AuctionHouseOfferPage auctionHouseOfferPage = new AuctionHouseOfferPage(serializedOffers, 1);
        AuctionHouseSellerOffersResponse response = new AuctionHouseSellerOffersResponse(request.getUniqueId(), auctionHouseOfferPage);
        this.redisMessenger.publish("callbacks", response);

    }

}

package io.github.flamehub.auctionhouse.commons.page;

import io.github.flamehub.auctionhouse.commons.category.AuctionHouseCategory;
import io.github.flamehub.auctionhouse.commons.offer.AuctionHouseOfferSort;
import io.github.flamehub.commons.messenger.packet.PacketRequest;

public final class AuctionHouseOfferPageRequest extends PacketRequest {

    private int pageId;
    private AuctionHouseOfferSort sort;
    private AuctionHouseCategory category;

    public AuctionHouseOfferPageRequest() {

    }

    public AuctionHouseOfferPageRequest(int pageId, AuctionHouseOfferSort sort, AuctionHouseCategory category) {
        this.pageId = pageId;
        this.sort = sort;
        this.category = category;
    }

    public int getPageId() {
        return pageId;
    }

    public AuctionHouseOfferSort getSort() {
        return sort;
    }

    public AuctionHouseCategory getCategory() {
        return category;
    }
}

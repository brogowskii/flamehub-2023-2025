package io.github.flamehub.auctionhouse.commons.page;

import java.io.Serializable;
import java.util.List;

public final class AuctionHouseOfferPage implements Serializable {

    private final List<String> jsonOffers;
    private final int maxPage;

    public AuctionHouseOfferPage(List<String> jsonOffers, int maxPage) {
        this.jsonOffers = jsonOffers;
        this.maxPage = maxPage;
    }

    public List<String> getJsonOffers() {
        return jsonOffers;
    }

    public int getMaxPage() {
        return maxPage;
    }
}

package io.github.flamehub.auctionhouse.commons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import io.github.flamehub.auctionhouse.commons.offer.AuctionHouseOffer;
import io.github.flamehub.auctionhouse.commons.offer.AuctionHouseOfferAdapter;
import io.github.flamehub.commons.json.InstantAdapter;

import java.time.Instant;

public final class AuctionHouseJsonUtil {

    public final static Gson GSON = new GsonBuilder()
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .registerTypeAdapter(AuctionHouseOffer.class, new AuctionHouseOfferAdapter())
            .serializeNulls()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

}

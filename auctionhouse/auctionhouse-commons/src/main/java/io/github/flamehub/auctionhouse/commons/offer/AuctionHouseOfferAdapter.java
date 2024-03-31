package io.github.flamehub.auctionhouse.commons.offer;

import com.google.gson.*;
import io.github.flamehub.auctionhouse.commons.AuctionHouseSeller;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public final class AuctionHouseOfferAdapter implements JsonSerializer<AuctionHouseOffer>, JsonDeserializer<AuctionHouseOffer> {

    @Override
    public AuctionHouseOffer deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = jsonElement.getAsJsonObject();

        return new AuctionHouseOffer(
                UUID.fromString(object.get("offerId").getAsString()),
                new AuctionHouseSeller(UUID.fromString(object.get("sellerUniqueId").getAsString()), object.get("sellerName").getAsString()),
                new AuctionHouseOfferItem(object.get("itemStack").getAsString(), object.get("material").getAsString()),
                BigDecimal.valueOf(object.get("price").getAsDouble()),
                Instant.ofEpochMilli(object.get("expirationTime").getAsLong())
        );
    }

    @Override
    public JsonElement serialize(AuctionHouseOffer offer, Type type, JsonSerializationContext context) {
        final JsonObject object = new JsonObject();

        object.addProperty("offerId", offer.getOfferId().toString());
        object.addProperty("sellerUniqueId", offer.getSeller().getUniqueId().toString());
        object.addProperty("sellerName", offer.getSeller().getName());
        object.addProperty("itemStack", offer.getItem().getSerializedItemStack());
        object.addProperty("material", offer.getItem().getMaterial());
        object.addProperty("price", offer.getPrice().doubleValue());
        object.addProperty("expirationTime", offer.getExpirationTime().toEpochMilli());

        return object;
    }

}
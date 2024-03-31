package io.github.flamehub.commons.bukkit.util;

import com.google.gson.*;
import org.bukkit.Location;

import java.lang.reflect.Type;

public class LocationAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {
    @Override
    public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return LocationUtil.deserialize(json.getAsString());
    }

    @Override
    public JsonElement serialize(Location src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(LocationUtil.serialize(src));
    }
}

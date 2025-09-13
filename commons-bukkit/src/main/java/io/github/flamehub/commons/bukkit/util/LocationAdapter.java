package io.github.flamehub.commons.bukkit.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import org.bukkit.Location;

public final class LocationAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {

  @Override
  public Location deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
      throws JsonParseException {
    return LocationUtil.deserialize(json.getAsString());
  }

  @Override
  public JsonElement serialize(final Location src, final Type typeOfSrc, final JsonSerializationContext context) {
    return new JsonPrimitive(LocationUtil.serialize(src));
  }
}

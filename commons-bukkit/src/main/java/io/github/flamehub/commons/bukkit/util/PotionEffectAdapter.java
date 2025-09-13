package io.github.flamehub.commons.bukkit.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class PotionEffectAdapter implements JsonSerializer<PotionEffect>,
    JsonDeserializer<PotionEffect> {

  public static String serializePotionEffect(final PotionEffect effect) {
    if (effect == null) {
      return "";
    }
    return effect.getType().getName() + ":" + effect.getDuration() + ":" + effect.getAmplifier();
  }

  public static PotionEffect deserializePotionEffect(final String serialized) {
    if (serialized == null || serialized.isEmpty()) {
      return null;
    }

    final String[] parts = serialized.split(":");
    final PotionEffectType type = PotionEffectType.getByName(parts[0]);
    final int duration = Integer.parseInt(parts[1]);
    final int amplifier = Integer.parseInt(parts[2]);

    return new PotionEffect(type, duration, amplifier);
  }

  @Override
  public PotionEffect deserialize(final JsonElement json, final Type typeOfT,
      final JsonDeserializationContext context) throws JsonParseException {
    return deserializePotionEffect(json.getAsString());
  }

  @Override
  public JsonElement serialize(final PotionEffect src, final Type typeOfSrc, final JsonSerializationContext context) {
    return new JsonPrimitive(serializePotionEffect(src));
  }
}

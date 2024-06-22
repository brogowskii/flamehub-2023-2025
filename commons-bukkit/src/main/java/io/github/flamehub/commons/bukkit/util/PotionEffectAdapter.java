package io.github.flamehub.commons.bukkit.util;

import com.google.gson.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Type;

public final class PotionEffectAdapter implements JsonSerializer<PotionEffect>, JsonDeserializer<PotionEffect> {
    @Override
    public PotionEffect deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return deserializePotionEffect(json.getAsString());
    }

    @Override
    public JsonElement serialize(PotionEffect src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(serializePotionEffect(src));
    }

    public static String serializePotionEffect(PotionEffect effect) {
        if (effect == null) {
            return "";
        }
        return effect.getType().getName() + ":" + effect.getDuration() + ":" + effect.getAmplifier();
    }

    public static PotionEffect deserializePotionEffect(String serialized) {
        if (serialized == null || serialized.isEmpty()) {
            return null;
        }

        String[] parts = serialized.split(":");
        PotionEffectType type = PotionEffectType.getByName(parts[0]);
        int duration = Integer.parseInt(parts[1]);
        int amplifier = Integer.parseInt(parts[2]);

        return new PotionEffect(type, duration, amplifier);
    }
}

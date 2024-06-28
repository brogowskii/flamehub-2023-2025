package io.github.flamehub.commons.bukkit.util;

import com.google.gson.*;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Type;
import java.time.Duration;

public final class GsonAdapters {

    public static class ItemStackSerializer implements JsonSerializer<ItemStack> {

        @Override
        public JsonElement serialize(ItemStack itemStack, Type typeOfSrc, JsonSerializationContext context) {
            ReadWriteNBT nbt = NBT.itemStackToNBT(itemStack);
            return new JsonPrimitive(nbt.toString());
        }
    }

    public static class ItemStackDeserializer implements JsonDeserializer<ItemStack> {

        @Override
        public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String tag = json.getAsString();
            ReadWriteNBT nbt = NBT.parseNBT(tag);
            return NBT.itemStackFromNBT(nbt);
        }
    }

    public static class LocationSerializer implements JsonSerializer<Location> {

        @Override
        public JsonElement serialize(Location location, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(LocationUtil.serialize(location));
        }
    }

    public static class LocationDeserializer implements JsonDeserializer<Location> {

        @Override
        public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocationUtil.deserialize(json.getAsString());
        }
    }

    public static class PotionEffectSerializer implements JsonSerializer<PotionEffect> {

        @Override
        public JsonElement serialize(PotionEffect potionEffect, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(serializePotionEffect(potionEffect));
        }
    }

    public static class PotionEffectDeserializer implements JsonDeserializer<PotionEffect> {

        @Override
        public PotionEffect deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return deserializePotionEffect(json.getAsString());
        }
    }

    public static class DurationSerializer implements JsonSerializer<Duration> {

        @Override
        public JsonElement serialize(Duration value, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(value.toString());
        }
    }

    public static class DurationDeserializer implements JsonDeserializer<Duration> {

        @Override
        public Duration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Duration.parse(json.getAsString());
        }
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

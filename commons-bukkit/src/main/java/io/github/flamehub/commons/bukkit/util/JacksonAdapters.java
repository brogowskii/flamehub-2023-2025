package io.github.flamehub.commons.bukkit.util;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;
import java.time.Duration;

public final class JacksonAdapters {

    public static class ItemStackSerializer extends JsonSerializer<ItemStack> {

        @Override
        public void serialize(ItemStack itemStack, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            ReadWriteNBT nbt = NBT.itemStackToNBT(itemStack);
            jsonGenerator.writeString(nbt.toString());
        }
    }

    public static class ItemStackDeserializer extends JsonDeserializer<ItemStack> {

        @Override
        public ItemStack deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            String tag = jsonParser.getText();
            ReadWriteNBT nbt = NBT.parseNBT(tag);
            return NBT.itemStackFromNBT(nbt);
        }
    }

    public static class LocationSerializer extends JsonSerializer<Location> {

        @Override
        public void serialize(Location location, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(LocationUtil.serialize(location));
        }
    }

    public static class LocationDeserializer extends JsonDeserializer<Location> {

        @Override
        public Location deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            return LocationUtil.deserialize(jsonParser.getText());
        }
    }

    public static class PotionEffectSerializer extends JsonSerializer<PotionEffect> {

        @Override
        public void serialize(PotionEffect potionEffect, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(serializePotionEffect(potionEffect));
        }
    }

    public static class PotionEffectDeserializer extends JsonDeserializer<PotionEffect> {

        @Override
        public PotionEffect deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            return deserializePotionEffect(jsonParser.getText());
        }
    }

    public static class DurationSerializer extends JsonSerializer<Duration> {

        @Override
        public void serialize(Duration value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.toString());
        }
    }

    public static class DurationDeserializer extends JsonDeserializer<Duration> {
        @Override
        public Duration deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
            String durationStr = p.getText();
            return Duration.parse(durationStr);
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

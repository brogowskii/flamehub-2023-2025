package io.github.flamehub.commons.bukkit.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class JacksonAdapters {

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

  public static class ItemStackSerializer extends JsonSerializer<ItemStack> {

    @Override
    public void serialize(final ItemStack itemStack, final JsonGenerator jsonGenerator,
        final SerializerProvider serializerProvider) throws IOException {
      final ReadWriteNBT nbt = NBT.itemStackToNBT(itemStack);
      jsonGenerator.writeString(nbt.toString());
    }
  }

  public static class ItemStackDeserializer extends JsonDeserializer<ItemStack> {

    @Override
    public ItemStack deserialize(final JsonParser jsonParser,
        final DeserializationContext deserializationContext) throws IOException {
      final String tag = jsonParser.getText();
      final ReadWriteNBT nbt = NBT.parseNBT(tag);
      return NBT.itemStackFromNBT(nbt);
    }
  }

  public static class LocationSerializer extends JsonSerializer<Location> {

    @Override
    public void serialize(final Location location, final JsonGenerator gen, final SerializerProvider serializers)
        throws IOException {
      gen.writeString(LocationUtil.serialize(location));
    }
  }

  public static class LocationDeserializer extends JsonDeserializer<Location> {

    @Override
    public Location deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
      return LocationUtil.deserialize(p.getText());
    }
  }

  public static class PotionEffectSerializer extends JsonSerializer<PotionEffect> {

    @Override
    public void serialize(final PotionEffect potionEffect, final JsonGenerator jsonGenerator,
        final SerializerProvider serializerProvider) throws IOException {
      jsonGenerator.writeString(serializePotionEffect(potionEffect));
    }
  }

  public static class PotionEffectDeserializer extends JsonDeserializer<PotionEffect> {

    @Override
    public PotionEffect deserialize(final JsonParser jsonParser,
        final DeserializationContext deserializationContext) throws IOException {
      return deserializePotionEffect(jsonParser.getText());
    }
  }

  public static class DurationSerializer extends JsonSerializer<Duration> {

    @Override
    public void serialize(final Duration value, final JsonGenerator gen, final SerializerProvider serializers)
        throws IOException {
      gen.writeString(value.toString());
    }
  }

  public static class DurationDeserializer extends JsonDeserializer<Duration> {

    @Override
    public Duration deserialize(final JsonParser p, final DeserializationContext ctx) throws IOException {
      final String durationStr = p.getText();
      return Duration.parse(durationStr);
    }
  }

  public static class InstantSerializer extends JsonSerializer<Instant> {

    @Override
    public void serialize(final Instant value, final JsonGenerator gen, final SerializerProvider serializers)
        throws IOException {
      gen.writeString(value.toString());
    }
  }

  public static class InstantDeserializer extends JsonDeserializer<Instant> {

    @Override
    public Instant deserialize(final JsonParser p, final DeserializationContext ctx) throws IOException {
      final String durationStr = p.getText();
      return Instant.parse(durationStr);
    }
  }

}

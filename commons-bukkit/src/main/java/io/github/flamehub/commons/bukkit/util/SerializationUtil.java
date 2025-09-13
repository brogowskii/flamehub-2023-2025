package io.github.flamehub.commons.bukkit.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public final class SerializationUtil {

  private SerializationUtil() {
  }

  public static String serializeBukkitObject(final Object object) {
    try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(
            byteArrayOutputStream)) {
      bukkitObjectOutputStream.writeObject(object);
      return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
    } catch (final Exception ex) {
      throw new IllegalStateException("Unable to serialize object", ex);
    }
  }

  public static Object deserializeBukkitObject(final String base64) {
    try (final BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(
        new ByteArrayInputStream(Base64.getDecoder().decode(base64)))) {
      return bukkitObjectInputStream.readObject();
    } catch (final Exception ex) {
      throw new IllegalStateException("Unable to deserialize object", ex);
    }
  }

  public static byte[] serializeBukkitObjectToBytes(final Object object) {
    try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(
            byteArrayOutputStream)) {
      bukkitObjectOutputStream.writeObject(object);
      return Base64.getEncoder().encode(byteArrayOutputStream.toByteArray());
    } catch (final Exception ex) {
      throw new IllegalStateException("Unable to serialize object", ex);
    }
  }

  public static Object deserializeBukkitObjectFromBytes(final byte[] base64) {
    try (final BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(
        new ByteArrayInputStream(Base64.getDecoder().decode(base64)))) {
      return bukkitObjectInputStream.readObject();
    } catch (final Exception ex) {
      throw new IllegalStateException("Unable to deserialize object", ex);
    }
  }

}

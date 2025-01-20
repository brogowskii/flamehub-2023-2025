package io.github.flamehub.marketplace;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public final class MarketSerializer {

  public static String serialize(final ItemStack item) {
    try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      final BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
      dataOutput.writeObject(item);
      return Base64Coder.encodeLines(outputStream.toByteArray());
    } catch (IllegalArgumentException | IOException e) {
      throw new IllegalStateException("Unable to save itemStack", e);
    }
  }

  public static ItemStack deserialize(final String data) throws IOException {
    try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(
        Base64Coder.decodeLines(data))) {
      final BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
      return (ItemStack) dataInput.readObject();
    } catch (ClassNotFoundException | IOException e) {
      throw new IOException("Unable to read class type", e);
    }
  }

}

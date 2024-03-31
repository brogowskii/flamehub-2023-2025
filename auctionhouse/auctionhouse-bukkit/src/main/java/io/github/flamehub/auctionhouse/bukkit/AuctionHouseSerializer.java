package io.github.flamehub.auctionhouse.bukkit;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AuctionHouseSerializer {

    public static String serialize(ItemStack item){
        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(item);
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (IllegalArgumentException | IOException e) {
            throw new IllegalStateException("Unable to save itemStack", e);
        }
    }

    public static ItemStack deserialize(String data) throws IOException{
        try(ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data))) {
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            return (ItemStack) dataInput.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new IOException("Unable to read class type", e);
        }
    }

}

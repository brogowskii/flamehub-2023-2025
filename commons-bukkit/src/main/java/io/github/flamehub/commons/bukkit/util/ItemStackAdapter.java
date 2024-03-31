package io.github.flamehub.commons.bukkit.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ItemStackAdapter extends TypeAdapter<ItemStack> {

    @Override
    public void write(JsonWriter out, ItemStack value) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

        dataOutput.writeObject(value);
        dataOutput.close();

        String trim = Base64.getEncoder()
                .encodeToString(outputStream.toByteArray())
                .trim();
        out.value(trim);
    }

    @Override
    public ItemStack read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }

        String data = reader.nextString();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
        BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

        ItemStack item;
        try {
            item = (ItemStack) dataInput.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        dataInput.close();
        return item;
    }

//    @Override
//    public void write(JsonWriter writer, ItemStack value) throws IOException {
//        if (value == null) {
//            writer.nullValue();
//            return;
//        }
//        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(value);
//        NBTTagCompound nbt = new NBTTagCompound();
//        nmsItem.b(nbt);
//        writer.value(nbt.toString());
//    }
//
//    @Override
//    public ItemStack read(JsonReader reader) throws IOException {
//        if (reader.peek() == JsonToken.NULL) {
//            reader.nextNull();
//            return null;
//        }
//        String tag = reader.nextString();
//        NBTTagCompound nbt;
//        try {
//            nbt = MojangsonParser.a(tag);
//        } catch (CommandSyntaxException e) {
//            throw new RuntimeException(e);
//        }
//        net.minecraft.world.item.ItemStack nbtItem = net.minecraft.world.item.ItemStack.a(nbt);
//        return CraftItemStack.asBukkitCopy(nbtItem);
//    }


}
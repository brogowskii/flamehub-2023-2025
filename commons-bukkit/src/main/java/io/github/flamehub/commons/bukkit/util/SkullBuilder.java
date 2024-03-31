package io.github.flamehub.commons.bukkit.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class SkullBuilder {

    private static final UUID RANDOM_UUID = UUID.fromString("92864445-51c5-4c3b-9039-517c9927d1b4");

    private static PlayerProfile getProfile(String url) {
        PlayerProfile profile = Bukkit.createProfile(RANDOM_UUID);
        PlayerTextures textures = profile.getTextures();
        URL urlObject;
        try {
            urlObject = new URL("https://textures.minecraft.net/texture/" + url);
        } catch (MalformedURLException exception) {
            throw new RuntimeException("Invalid URL", exception);
        }
        textures.setSkin(urlObject);
        profile.setTextures(textures);
        return profile;
    }

    public static ItemStack create(String url) {
        PlayerProfile profile = getProfile(url);
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setPlayerProfile(profile);
        head.setItemMeta(meta);
        return head;
    }

    public static ItemStack create(@NotNull Player player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setPlayerProfile(player.getPlayerProfile());
        head.setItemMeta(meta);
        return head;
    }

}
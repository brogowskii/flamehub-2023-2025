package io.github.flamehub.commons.bukkit.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;

public final class SkullBuilder {

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

  public static ItemStack createFromBase64(String base64) {
    try {
      URL url = getUrlFromBase64(base64);
      return create(url.toString());
    } catch (MalformedURLException exception) {
      throw new RuntimeException("Invalid base64", exception);
    }
  }

  public static ItemStack create(@NotNull Player player) {
    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
    SkullMeta meta = (SkullMeta) head.getItemMeta();
    meta.setPlayerProfile(player.getPlayerProfile());
    head.setItemMeta(meta);
    return head;
  }

  public static URL getUrlFromBase64(String base64) throws MalformedURLException {
    String decoded = new String(Base64.getDecoder().decode(base64));
    // We simply remove the "beginning" and "ending" part of the JSON, so we're left with only the URL. You could use a proper
    // JSON parser for this, but that's not worth it. The String will always start exactly with this stuff anyway
    return new URL(decoded.substring("{\"textures\":{\"SKIN\":{\"url\":\"".length(),
        decoded.length() - "\"}}}".length()));
  }

  public static ItemStack getHeadFrom64(String value) {
    ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
    SkullMeta meta = (SkullMeta) head.getItemMeta();
    PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
    profile.setProperty(new ProfileProperty("textures", value));
    meta.setPlayerProfile(profile);
    head.setItemMeta(meta);

    return head;
  }


}
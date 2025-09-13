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

  private static PlayerProfile getProfile(final String url) {
    final PlayerProfile profile = Bukkit.createProfile(RANDOM_UUID);
    final PlayerTextures textures = profile.getTextures();
    final URL urlObject;
    try {
      urlObject = new URL("https://textures.minecraft.net/texture/" + url);
    } catch (final MalformedURLException exception) {
      throw new RuntimeException("Invalid URL", exception);
    }
    textures.setSkin(urlObject);
    profile.setTextures(textures);
    return profile;
  }

  public static ItemStack create(final String url) {
    final PlayerProfile profile = getProfile(url);
    final ItemStack head = new ItemStack(Material.PLAYER_HEAD);
    final SkullMeta meta = (SkullMeta) head.getItemMeta();
    meta.setPlayerProfile(profile);
    head.setItemMeta(meta);
    return head;
  }

  public static ItemStack createFromBase64(final String base64) {
    try {
      final URL url = getUrlFromBase64(base64);
      return create(url.toString());
    } catch (final MalformedURLException exception) {
      throw new RuntimeException("Invalid base64", exception);
    }
  }

  public static ItemStack create(@NotNull final Player player) {
    final ItemStack head = new ItemStack(Material.PLAYER_HEAD);
    final SkullMeta meta = (SkullMeta) head.getItemMeta();
    meta.setPlayerProfile(player.getPlayerProfile());
    head.setItemMeta(meta);
    return head;
  }

  public static URL getUrlFromBase64(final String base64) throws MalformedURLException {
    final String decoded = new String(Base64.getDecoder().decode(base64));
    // We simply remove the "beginning" and "ending" part of the JSON, so we're left with only the URL. You could use a proper
    // JSON parser for this, but that's not worth it. The String will always start exactly with this stuff anyway
    return new URL(decoded.substring("{\"textures\":{\"SKIN\":{\"url\":\"".length(),
        decoded.length() - "\"}}}".length()));
  }

  public static ItemStack getHeadFrom64(final String value) {
    final ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
    final SkullMeta meta = (SkullMeta) head.getItemMeta();
    final PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
    profile.setProperty(new ProfileProperty("textures", value));
    meta.setPlayerProfile(profile);
    head.setItemMeta(meta);

    return head;
  }


}
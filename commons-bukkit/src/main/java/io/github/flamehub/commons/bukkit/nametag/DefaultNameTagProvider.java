package io.github.flamehub.commons.bukkit.nametag;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public final class DefaultNameTagProvider implements NameTagProvider {

  @Override
  public String getName(Player player) {
    return player.getName();
  }

  @Override
  public String getPrefix(Player target, Player receiver) {
    return PlaceholderAPI.setPlaceholders(target, "%luckperms_prefix%");
  }

  @Override
  public String getSuffix(Player target, Player receiver) {
    return "";
  }

  @Override
  public NamedTextColor getColor(Player player) {
    return NamedTextColor.WHITE;
  }
}

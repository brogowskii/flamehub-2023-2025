package io.github.flamehub.commons.bukkit.nametag;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public final class DefaultNameTagProvider implements NameTagProvider {

  @Override
  public String getName(final Player player) {
    return player.getName();
  }

  @Override
  public String getPrefix(final Player target, final Player receiver) {
    return PlaceholderAPI.setPlaceholders(target, "%luckperms_prefix%");
  }

  @Override
  public String getSuffix(final Player target, final Player receiver) {
    return "";
  }

  @Override
  public NamedTextColor getColor(final Player player) {
    return NamedTextColor.WHITE;
  }
}

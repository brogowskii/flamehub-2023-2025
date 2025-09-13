package io.github.flamehub.lobby.nametag;

import io.github.flamehub.commons.bukkit.nametag.NameTagProvider;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class NameTagProviderImpl implements NameTagProvider {

  @Override
  public String getName(final Player player) {
    return player.getName();
  }

  @Override
  public String getPrefix(final Player target, final Player receiver) {
    return PlaceholderAPI.setPlaceholders(target, "%lobby_prefix%");
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

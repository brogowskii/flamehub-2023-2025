package io.github.flamehub.commons.bukkit.nametag;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public interface NameTagProvider {

  String getName(Player player);

  String getPrefix(Player target, Player receiver);

  String getSuffix(Player target, Player receiver);

  NamedTextColor getColor(Player player);


  default NamedTextColor getColorFrom(final String text) {

    final NamedTextColor color;
    switch (text) {
      case "&1" -> color = NamedTextColor.DARK_BLUE;
      case "&2" -> color = NamedTextColor.DARK_GREEN;
      case "&3" -> color = NamedTextColor.DARK_AQUA;
      case "&4" -> color = NamedTextColor.DARK_RED;
      case "&5" -> color = NamedTextColor.DARK_PURPLE;
      case "&6" -> color = NamedTextColor.GOLD;
      case "&7" -> color = NamedTextColor.GRAY;
      case "&8" -> color = NamedTextColor.DARK_GRAY;
      case "&9" -> color = NamedTextColor.BLUE;
      case "&0" -> color = NamedTextColor.BLACK;
      case "&b" -> color = NamedTextColor.AQUA;
      case "&c" -> color = NamedTextColor.RED;
      case "&a" -> color = NamedTextColor.GREEN;
      default -> color = NamedTextColor.WHITE;
    }

    return color;

  }

}

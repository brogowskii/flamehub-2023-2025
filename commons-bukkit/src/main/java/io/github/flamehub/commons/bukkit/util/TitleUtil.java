package io.github.flamehub.commons.bukkit.util;

import io.github.flamehub.commons.bukkit.text.TextUtil;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.entity.Player;

public final class TitleUtil {

  private TitleUtil() {
  }

  public static void title(
      final Player player, final String title, final String subTitle, final int fadeIn, final int stay, final int fadeOut) {
    player.showTitle(Title.title(
        TextUtil.parse(title),
        TextUtil.parse(subTitle),
        Title.Times.times(Ticks.duration(fadeIn), Ticks.duration(stay), Ticks.duration(fadeOut)))
    );
  }

}


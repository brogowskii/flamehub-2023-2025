package io.github.flamehub.commons.bukkit.util;

import io.github.flamehub.commons.bukkit.text.TextUtil;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.entity.Player;

public final class TitleUtil {

  private TitleUtil() {
  }

  public static void title(
      Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
    player.showTitle(Title.title(
        TextUtil.MINI_MESSAGE.deserialize(title),
        TextUtil.MINI_MESSAGE.deserialize(subTitle),
        Title.Times.times(Ticks.duration(fadeIn), Ticks.duration(stay), Ticks.duration(fadeOut)))
    );
  }

}


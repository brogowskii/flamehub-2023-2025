package io.github.flamehub.commons.bukkit.tab;

import io.github.flamehub.commons.bukkit.text.TextUtil;
import java.util.List;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.entity.Player;

public final class HeaderFooterTablistService implements TablistService {

  private final TablistConfig tablistConfig;

  public HeaderFooterTablistService(final TablistConfig tablistConfig) {
    this.tablistConfig = tablistConfig;
  }

  public void send(final Player player) {

    final List<Component> header = TextUtil.parse(PlaceholderAPI.setPlaceholders(player, tablistConfig.getHeader()));
    final List<Component> footer = TextUtil.parse(PlaceholderAPI.setPlaceholders(player, tablistConfig.getFooter()));

    final JoinConfiguration separator = JoinConfiguration.separator(Component.newline());
    player.sendPlayerListHeaderAndFooter(Component.join(separator, header),
        Component.join(separator, footer));
  }

}

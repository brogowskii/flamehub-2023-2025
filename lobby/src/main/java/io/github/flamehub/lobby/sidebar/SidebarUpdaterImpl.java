package io.github.flamehub.lobby.sidebar;

import fr.mrmicky.fastboard.FastBoard;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.sidebar.SidebarUpdater;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import java.util.List;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public final class SidebarUpdaterImpl implements SidebarUpdater {

  private final BukkitMessagesService messagesService;

  public SidebarUpdaterImpl(final BukkitMessagesService messagesService) {
    this.messagesService = messagesService;
  }

  @Override
  public String getTitle(final FastBoard sidebar) {
    final Player player = sidebar.getPlayer();
    final String message = messagesService.getMessage("sidebar.lobby.title");
    final String withPlaceholders = PlaceholderAPI.setPlaceholders(player, message);
    return TextUtil.legacyColor(withPlaceholders);
  }

  @Override
  public List<String> getLines(final FastBoard sidebar) {
    final Player player = sidebar.getPlayer();
    final List<String> messages = messagesService.getMessages("sidebar.lobby.lines");
    final List<String> withPlaceholders = PlaceholderAPI.setPlaceholders(player, messages);
    return TextUtil.legacyColor(withPlaceholders);
  }
}
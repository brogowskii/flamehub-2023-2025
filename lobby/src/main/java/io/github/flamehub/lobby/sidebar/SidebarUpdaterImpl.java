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

  public SidebarUpdaterImpl(BukkitMessagesService messagesService) {
    this.messagesService = messagesService;
  }

  @Override
  public String getTitle(FastBoard sidebar) {
    Player player = sidebar.getPlayer();
    String message = this.messagesService.getMessage("sidebar.lobby.title");
    String withPlaceholders = PlaceholderAPI.setPlaceholders(player, message);
    return TextUtil.legacyColor(withPlaceholders);
  }

  @Override
  public List<String> getLines(FastBoard sidebar) {
    Player player = sidebar.getPlayer();
    List<String> messages = this.messagesService.getMessages("sidebar.lobby.lines");
    List<String> withPlaceholders = PlaceholderAPI.setPlaceholders(player, messages);
    return TextUtil.legacyColor(withPlaceholders);
  }
}
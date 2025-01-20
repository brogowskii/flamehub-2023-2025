package io.github.flamehub.commons.bukkit.tab;

import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import java.util.List;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public final class DefaultTablistProvider implements TablistProvider {

  private final BukkitMessagesService messagesService;

  public DefaultTablistProvider(BukkitMessagesService messagesService) {
    this.messagesService = messagesService;
  }

  @Override
  public List<String> getHeader(Player player) {
    return PlaceholderAPI.setPlaceholders(player,
        messagesService.getMessages("default.tablist.header"));
  }

  @Override
  public List<String> getFooter(Player player) {
    return PlaceholderAPI.setPlaceholders(player,
        messagesService.getMessages("default.tablist.footer"));
  }
}

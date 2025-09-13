package io.github.flamehub.commons.bukkit.actionbar;

import io.github.flamehub.commons.bukkit.text.TextUtil;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ActionBarTask implements Runnable {

  private StringBuilder builder;

  @Override
  public void run() {
    for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      if (onlinePlayer == null) {
        continue;
      }

      show(onlinePlayer);
    }
  }

  void show(final Player player) {
    final long currentTimeMillis = System.currentTimeMillis();
    final Set<ActionBarNotice> notices =
        new HashSet<>(ActionBarNoticeCache.values(player.getUniqueId()).values());

    if (notices.isEmpty()) {
      return;
    }

    builder = new StringBuilder();
    for (final ActionBarNotice notice :
        notices.stream()
            .sorted(Comparator.comparingInt(ActionBarNotice::getPriority))
            .collect(Collectors.toCollection(LinkedHashSet::new))) {

      if ((notice.getExpireTime() > currentTimeMillis || notice.getExpireTime() == 0)) {
        updateMessage(notice.getText());
      } else {
        ActionBarNoticeCache.remove(player.getUniqueId(), notice.getType());
      }
    }

    player.sendActionBar(TextUtil.parse(builder.toString()));
  }

  void updateMessage(final String text) {
    if (builder.toString().isEmpty()) {
      builder.append(text);
      return;
    }

    builder.append(" &8〣 &r").append(text);
  }
}
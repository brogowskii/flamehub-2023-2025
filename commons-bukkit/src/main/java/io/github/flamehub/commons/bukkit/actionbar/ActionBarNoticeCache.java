package io.github.flamehub.commons.bukkit.actionbar;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ActionBarNoticeCache {

  private static final Map<UUID, Map<String, ActionBarNotice>> actionBars =
      new ConcurrentHashMap<>();

  public static void add(final UUID uuid, final ActionBarNotice actionBar) {
    final Map<String, ActionBarNotice> actionBarNoticeMap = values(uuid);
    final ActionBarNotice actionBarNotice = actionBarNoticeMap.get(actionBar.getType());
    if (actionBarNotice != null) {
      actionBarNotice.setText(actionBar.getText());
      actionBarNotice.setExpireTime(actionBar.getExpireTime());
      return;
    }

    actionBarNoticeMap.put(actionBar.getType(), actionBar);
  }

  public static void remove(final UUID uuid, final String type) {
    values(uuid).remove(type);
  }

  public static Map<String, ActionBarNotice> values(final UUID uuid) {
    return actionBars.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>());
  }
}

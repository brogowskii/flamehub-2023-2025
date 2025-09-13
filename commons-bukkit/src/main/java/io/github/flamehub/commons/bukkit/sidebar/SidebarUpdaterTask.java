package io.github.flamehub.commons.bukkit.sidebar;

import fr.mrmicky.fastboard.FastBoard;
import java.util.ArrayList;
import java.util.List;

public final class SidebarUpdaterTask implements Runnable {

  private final SidebarCache sidebarCache;
  private final SidebarUpdater sidebarUpdater;

  public SidebarUpdaterTask(final SidebarCache sidebarCache, final SidebarUpdater sidebarUpdater) {
    this.sidebarCache = sidebarCache;
    this.sidebarUpdater = sidebarUpdater;
  }

  @Override
  public void run() {
    final List<FastBoard> fastBoards = new ArrayList<>(sidebarCache.values());

    for (final FastBoard value : fastBoards) {
      if (value.getPlayer() == null || !value.getPlayer().isOnline()) {
        continue;
      }

      try {
        value.updateTitle(sidebarUpdater.getTitle(value));
        value.updateLines(sidebarUpdater.getLines(value));
      } catch (final Exception ignored) {
      }
    }
  }
}
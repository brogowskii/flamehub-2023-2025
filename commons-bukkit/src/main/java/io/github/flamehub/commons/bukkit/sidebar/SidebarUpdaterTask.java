package io.github.flamehub.commons.bukkit.sidebar;

import fr.mrmicky.fastboard.FastBoard;

public final class SidebarUpdaterTask implements Runnable {

    private final SidebarCache sidebarCache;
    private final SidebarUpdater sidebarUpdater;

    public SidebarUpdaterTask(SidebarCache sidebarCache, SidebarUpdater sidebarUpdater) {
        this.sidebarCache = sidebarCache;
        this.sidebarUpdater = sidebarUpdater;
    }

    @Override
    public void run() {

        for (FastBoard value : this.sidebarCache.values()) {

            if (value.getPlayer() == null) {
                continue;
            }

            value.updateTitle(this.sidebarUpdater.getTitle(value));
            value.updateLines(this.sidebarUpdater.getLines(value));

        }

    }
}
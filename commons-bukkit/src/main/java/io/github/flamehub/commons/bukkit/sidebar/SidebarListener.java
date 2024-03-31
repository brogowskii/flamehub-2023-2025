package io.github.flamehub.commons.bukkit.sidebar;

import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class SidebarListener implements Listener {

    private final SidebarCache sidebarCache;

    public SidebarListener(SidebarCache sidebarCache) {
        this.sidebarCache = sidebarCache;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        FastBoard fastBoard = new FastBoard(event.getPlayer());
        this.sidebarCache.add(event.getPlayer().getUniqueId(), fastBoard);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        FastBoard fastBoard = this.sidebarCache.findByKey(player.getUniqueId());
        if (fastBoard != null) {
            fastBoard.delete();
            this.sidebarCache.remove(player.getUniqueId());
        }

    }

}
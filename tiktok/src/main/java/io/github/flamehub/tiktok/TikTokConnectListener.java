package io.github.flamehub.tiktok;

import io.github.flamehub.commons.bukkit.user.event.AsyncPlayerJoinEvent;
import io.github.flamehub.commons.user.User;
import io.github.flamehub.tiktok.user.TikTokUser;
import io.github.flamehub.tiktok.user.TikTokUserCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public final class TikTokConnectListener implements Listener {

  private final Plugin plugin;
  private final TikTokUserCache tikTokUserCache;

  public TikTokConnectListener(final Plugin plugin, final TikTokUserCache tikTokUserCache) {
    this.plugin = plugin;
    this.tikTokUserCache = tikTokUserCache;
  }

  @EventHandler
  public void onJoin(final AsyncPlayerJoinEvent event) {
    final Player player = event.getPlayer();

    if (event.getUser() instanceof TikTokUser tikTokUser) {
      if (tikTokUser.getTikTokUsername() == null || tikTokUser.getTikTokUsername().isEmpty()) {
        final TikTokConnectTask tikTokConnectTask = new TikTokConnectTask(player);
        tikTokConnectTask.runTaskTimerAsynchronously(plugin, 0L, 20L);
      }

    }


  }

}

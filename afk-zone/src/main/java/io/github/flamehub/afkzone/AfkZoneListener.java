package io.github.flamehub.afkzone;

import java.util.UUID;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public final class AfkZoneListener implements Listener {

  private final AfkZoneConfig afkZoneConfig;

  public AfkZoneListener(AfkZoneConfig afkZoneConfig) {
    this.afkZoneConfig = afkZoneConfig;
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {

    Player player = event.getPlayer();
    UUID uniqueId = player.getUniqueId();
    for (AfkZoneReward afkZoneReward : this.afkZoneConfig.getAfkZoneRewards()) {
      if (afkZoneReward.getUuidInstantMap().get(uniqueId) != null) {
        afkZoneReward.getUuidInstantMap().remove(uniqueId);
      }

      BossBar bossBar = afkZoneReward.getBossBarMap().get(player.getUniqueId());
      if (bossBar != null) {
        bossBar.removePlayer(player);
        bossBar.setVisible(false);
        afkZoneReward.getBossBarMap().remove(player.getUniqueId());
      }


    }

  }


}

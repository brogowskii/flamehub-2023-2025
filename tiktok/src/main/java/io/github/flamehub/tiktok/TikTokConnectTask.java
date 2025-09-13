package io.github.flamehub.tiktok;

import io.github.flamehub.commons.bukkit.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public final class TikTokConnectTask extends BukkitRunnable {

  private final static double MAX_TIME = 30.0;
  private final BossBar bossBar;
  private int i = (int) MAX_TIME;

  public TikTokConnectTask(final Player player) {
    bossBar = Bukkit.createBossBar(
        TextUtil.legacyColor(
            "\uE02F &8| &cNie połączyłeś jeszcze swojego konta tiktok! Użyj &4/tiktok polacz"),
        BarColor.RED,
        BarStyle.SEGMENTED_20
    );
    bossBar.setVisible(true);
    bossBar.addPlayer(player);
    bossBar.setProgress(1.0F);
  }

  @Override
  public void run() {

    if (i <= 0) {
      bossBar.removeAll();
      bossBar.setVisible(false);
      cancel();
      return;
    }

    i--;
    bossBar.setProgress(i / MAX_TIME);

  }
}
package io.github.flamehub.timeplayed.user;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import io.github.flamehub.commons.bukkit.text.TextUtil;

public final class TimePlayedUserInfoRunnable extends BukkitRunnable {

    private final static double MAX_TIME = 10.0;
    private final BossBar bossBar;
    private int i = (int) MAX_TIME;

    public TimePlayedUserInfoRunnable(Player player) {
        this.bossBar = Bukkit.createBossBar(TextUtil.legacyColor("&5⌚ &8| &fOtrzymałeś &d1★ &fza &f5 min &fciągłej gry na serwerze!"), BarColor.PURPLE, BarStyle.SOLID);
        this.bossBar.setVisible(true);
        this.bossBar.addPlayer(player);
        this.bossBar.setProgress(1.0F);
    }

    @Override
    public void run() {

        if (i <= 0) {
            this.bossBar.removeAll();
            this.bossBar.setVisible(false);
            cancel();
            return;
        }

        i--;
        this.bossBar.setProgress(i / MAX_TIME);

    }
}
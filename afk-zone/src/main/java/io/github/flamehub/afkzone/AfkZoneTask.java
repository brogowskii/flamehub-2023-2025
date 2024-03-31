package io.github.flamehub.afkzone;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.TitleUtil;
import io.github.flamehub.commons.util.RandomUtil;
import io.github.flamehub.commons.util.RoundUtil;
import io.github.flamehub.commons.util.TimeUtil;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class AfkZoneTask implements Runnable {

    private final Plugin plugin;
    private final AfkZoneConfig afkZoneConfig;

    public AfkZoneTask(Plugin plugin, AfkZoneConfig afkZoneConfig) {
        this.plugin = plugin;
        this.afkZoneConfig = afkZoneConfig;
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(this::execute);
    }

    public void execute(Player player) {

        long now = System.currentTimeMillis();
        for (AfkZoneReward afkZoneReward : this.afkZoneConfig.getAfkZoneRewards()) {
            Map<UUID, Long> uuidInstantMap = afkZoneReward.getUuidInstantMap();

            Long instant = uuidInstantMap.get(player.getUniqueId());
            BossBar bossBar = afkZoneReward.getBossBarMap().get(player.getUniqueId());
            if (!isInside(player.getLocation())) {

                if (bossBar != null) {
                    bossBar.setVisible(false);
                    bossBar.removePlayer(player);
                    afkZoneReward.getBossBarMap().remove(player.getUniqueId());
                }

                if (instant != null) {
                    uuidInstantMap.remove(player.getUniqueId());
                }
                continue;
            }

            String title = afkZoneReward.getTitle();
            if (instant == null) {
                instant = now + TimeUnit.SECONDS.toMillis(afkZoneReward.getSeconds());
                uuidInstantMap.put(player.getUniqueId(), instant);

                if (bossBar == null) {
                    bossBar = Bukkit.createBossBar(TextUtil.color(afkZoneReward.getTitle()), afkZoneReward.getColor(), afkZoneReward.getStyle());
                    bossBar.setVisible(true);
                    bossBar.addPlayer(player);
                    afkZoneReward.getBossBarMap().put(player.getUniqueId(), bossBar);
                }

            }

            double chance = AfkZoneChances.getChance(player);
            if (now > instant) {

                if (afkZoneReward.getId().equalsIgnoreCase("premium")) {
                    if (RandomUtil.getChance(chance)) {
                        TitleUtil.title(player, "&5&lOtrzymano nagrodę!", "&fPomyślnie wylosowałeś &5klucz afk&f!", 20, 60, 20);
                        Bukkit.getScheduler().runTask(this.plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), afkZoneReward.getCommand()
                                .replace("{PLAYER}", player.getName())));
                    } else {
                        TitleUtil.title(player, "&5&lNie udało się :(", "&fTym razem nie wylosowałeś klucza, próbuj dalej!", 20, 60, 20);

                    }

                } else {
                    TitleUtil.title(player, "&5&lOtrzymano nagrodę!", "&fPomyślnie otrzymałeś podstawową nagrodę!", 20, 60, 20);
                    Bukkit.getScheduler().runTask(this.plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), afkZoneReward.getCommand()
                            .replace("{PLAYER}", player.getName())));
                }

                uuidInstantMap.remove(player.getUniqueId());
                bossBar.removePlayer(player);
                bossBar.setVisible(false);
                afkZoneReward.getBossBarMap().remove(player.getUniqueId());
                continue;
            }


            long timeSeconds = afkZoneReward.getSeconds();
            long durationSeconds = TimeUnit.MILLISECONDS.toSeconds(instant - now);
            double percentRemaining = 100.0 - ((double) durationSeconds / timeSeconds * 100.0);
            bossBar.setProgress(percentRemaining / 100.0);


            String s = TextUtil.legacyColor(title
                    .replace("{TIME}", TimeUtil.formatTime(Duration.between(Instant.ofEpochMilli(now), Instant.ofEpochMilli(instant))))
                    .replace("{CHANCE}", String.valueOf(chance))
                    .replace("{PERCENTAGE}", String.valueOf(RoundUtil.round(percentRemaining, 2))));
            bossBar.setTitle(s);
        }
    }

    public boolean isInside(Location location) {
        Location first = this.afkZoneConfig.getMinLocation();
        Location second = this.afkZoneConfig.getMaxLocation();

        int minX = Math.min(first.getBlockX(), second.getBlockX());
        int maxX = Math.max(first.getBlockX(), second.getBlockX());
        int minY = Math.min(first.getBlockY(), second.getBlockY());
        int maxY = Math.max(first.getBlockY(), second.getBlockY());
        int minZ = Math.min(first.getBlockZ(), second.getBlockZ());
        int maxZ = Math.max(first.getBlockZ(), second.getBlockZ());

        return ((location.getY() < maxY) && (location.getY() >= minY))
                && location.getBlockX() > minX
                && location.getBlockX() < maxX
                && location.getBlockZ() > minZ
                && location.getBlockZ() < maxZ;
    }
}

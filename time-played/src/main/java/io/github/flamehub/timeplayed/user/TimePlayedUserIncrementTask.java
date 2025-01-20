package io.github.flamehub.timeplayed.user;

import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class TimePlayedUserIncrementTask implements Runnable {

  private final Plugin plugin;
  private final TimePlayedUserCache timePlayedUserCache;

  public TimePlayedUserIncrementTask(Plugin plugin, TimePlayedUserCache timePlayedUserCache) {
    this.plugin = plugin;
    this.timePlayedUserCache = timePlayedUserCache;
  }

  @Override
  public void run() {
    long currentTimeMillis = System.currentTimeMillis();
    for (Player player : Bukkit.getOnlinePlayers()) {
      TimePlayedUser timePlayedUser = timePlayedUserCache.findByUniqueId(player.getUniqueId());
      if (timePlayedUser == null) {
        continue;
      }

      addTime(timePlayedUser, currentTimeMillis);
      addCoins(timePlayedUser, player, currentTimeMillis);

    }
  }

  void addCoins(TimePlayedUser timePlayedUser, Player player, long currentMillis) {
    long lastAddCoinsTimeMeasurement = timePlayedUser.getLastAddCoinsTimeMeasurement();
    if (lastAddCoinsTimeMeasurement == 0L) {
      return;
    }

    if (currentMillis > lastAddCoinsTimeMeasurement + TimeUnit.MINUTES.toMillis(5)) {

      timePlayedUser.setCoins(timePlayedUser.getCoins() + 1);
      timePlayedUser.setLastAddCoinsTimeMeasurement(currentMillis);
      timePlayedUser.setNeedUpdate(true);

      new TimePlayedUserInfoRunnable(player).runTaskTimer(plugin, 0L, 20L);
    }
  }

  void addTime(TimePlayedUser timePlayedUser, long currentMillis) {
    long lastSpendTimeMeasurement = timePlayedUser.getLastSpendTimeMeasurement();
    if (lastSpendTimeMeasurement == 0L) {
      return;
    }

    long time = timePlayedUser.getSpendTime() + (currentMillis - lastSpendTimeMeasurement);
    timePlayedUser.setSpendTime(time);
    timePlayedUser.setLastSpendTimeMeasurement(currentMillis);
    timePlayedUser.setNeedUpdate(true);
    plugin.getServer().getPluginManager()
        .callEvent(new SpendTimeIncrementEvent(timePlayedUser));
  }
}
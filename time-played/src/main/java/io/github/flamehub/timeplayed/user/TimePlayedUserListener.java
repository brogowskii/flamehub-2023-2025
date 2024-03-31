package io.github.flamehub.timeplayed.user;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import io.github.flamehub.commons.bukkit.user.event.AsyncPlayerJoinEvent;
import io.github.flamehub.commons.user.User;

public class TimePlayedUserListener implements Listener {

    private final TimePlayedUserCache timePlayedUserCache;

    public TimePlayedUserListener(TimePlayedUserCache timePlayedUserCache) {
        this.timePlayedUserCache = timePlayedUserCache;
    }

    @EventHandler
    public void onJoin(AsyncPlayerJoinEvent event) {
        User user = event.getUser();
        if (user instanceof TimePlayedUser timePlayedUser) {
            long currentTimeMillis = System.currentTimeMillis();
            timePlayedUser.setLastSpendTimeMeasurement(currentTimeMillis);
            timePlayedUser.setLastAddCoinsTimeMeasurement(System.currentTimeMillis());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        TimePlayedUser timePlayedUser = this.timePlayedUserCache.findByUniqueId(player.getUniqueId());
        if (timePlayedUser == null) {
            return;
        }

        long time = timePlayedUser.getSpendTime() + (System.currentTimeMillis() - timePlayedUser.getLastSpendTimeMeasurement());
        timePlayedUser.setSpendTime(time);
        timePlayedUser.setLastAddCoinsTimeMeasurement(0L);
    }
}
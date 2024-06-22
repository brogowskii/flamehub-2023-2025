package io.github.flamehub.timeplayed;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import io.github.flamehub.commons.util.TimeUtil;
import io.github.flamehub.timeplayed.user.TimePlayedUser;
import io.github.flamehub.timeplayed.user.TimePlayedUserCache;

public final class TimePlayedPlaceholder extends PlaceholderExpansion {

    private final static TimeUtil.TimeDivision[] DIVISIONS = new TimeUtil.TimeDivision[]{
            TimeUtil.TimeDivision.DAY,
            TimeUtil.TimeDivision.HOUR,
            TimeUtil.TimeDivision.MINUTE
    };

    private final TimePlayedUserCache timePlayedUserCache;

    public TimePlayedPlaceholder(TimePlayedUserCache timePlayedUserCache) {
        this.timePlayedUserCache = timePlayedUserCache;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "timeplayed";
    }

    @Override
    public @NotNull String getAuthor() {
        return "MarcinOpalka";
    }

    @Override
    public @NotNull String getVersion() {
        return "0.1";
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        Player player = offlinePlayer.getPlayer();
        if (player == null) {
            return "";
        }

        TimePlayedUser user = this.timePlayedUserCache.findByUniqueId(player.getUniqueId());
        if (user == null) {
            return "";
        }

        switch (params) {
            case "time" -> {
                return TimeUtil.formatTimeSimple(user.getSpendTime(), false);
            }
            case "coins" -> {
                return String.valueOf(user.getCoins());
            }
        }

        if (params.contains("time-player:")) {
            String[] split = params.split(":");
            TimePlayedUser targetUser = this.timePlayedUserCache.findByName(split[1]);
            return TimeUtil.formatTimeSimple(targetUser.getSpendTime(), false);
        }

        return "";
    }


}
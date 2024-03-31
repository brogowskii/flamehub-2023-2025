package io.github.flamehub.auctionhouse.bukkit;

import org.bukkit.entity.Player;

public final class AuctionHouseLimiter {

    public static int getLimit(Player player) {

        if (player.hasPermission("flamehub.auctionhouse.limit.30")) {
            return 30;
        }
        if (player.hasPermission("flamehub.auctionhouse.limit.20")) {
            return 20;
        }
        else if (player.hasPermission("flamehub.auctionhouse.limit.10")) {
            return 10;
        }
        else if (player.hasPermission("flamehub.auctionhouse.limit.5")) {
            return 5;
        }

        return 3;

    }

}

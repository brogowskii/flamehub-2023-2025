package io.github.flamehub.auctionhouse.bukkit;

import org.bukkit.entity.Player;

import java.util.Map;

public final class AuctionHouseLimiter {

    private final static Map<String, Integer> STRING_INTEGER_MAP = Map.of(
            "server.auctionhouse.limit.flame", 20,
            "server.auctionhouse.limit.mvip", 15,
            "server.auctionhouse.limit.svip", 10,
            "server.auctionhouse.limit.vip", 5,
            "server.auctionhouse.limit.gracz", 3
    );

    public static int getLimit(Player player) {
        int limit = 0;
        for (Map.Entry<String, Integer> entry : STRING_INTEGER_MAP.entrySet()) {
            String key = entry.getKey();
            if (player.hasPermission(key)) {
                Integer value = entry.getValue();
                if (value > limit) {
                    limit = value;
                }

            }

        }

        return limit;
    }



}

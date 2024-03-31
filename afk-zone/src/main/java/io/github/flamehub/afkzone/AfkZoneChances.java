package io.github.flamehub.afkzone;

import org.bukkit.entity.Player;

import java.util.Map;

public final class AfkZoneChances {

    private final static Map<String, Double> STRING_INTEGER_MAP = Map.of(
            "afk.zone.player", 30.0,
            "afk.zone.vip", 40.0,
            "afk.zone.svip", 50.0,
            "afk.zone.mvip", 55.0,
            "afk.zone.flame", 60.0
    );

    public static double getChance(Player player) {
        double limit = 0;
        for (Map.Entry<String, Double> entry : STRING_INTEGER_MAP.entrySet()) {
            String key = entry.getKey();
            if (player.hasPermission(key)) {
                Double value = entry.getValue();
                if (value > limit) {
                    limit = value;
                }

            }

        }

        return limit;
    }

}

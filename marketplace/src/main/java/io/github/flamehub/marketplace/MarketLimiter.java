package io.github.flamehub.marketplace;

import java.util.Map;
import org.bukkit.entity.Player;

public final class MarketLimiter {

  private final static Map<String, Integer> STRING_INTEGER_MAP = Map.of(
      "server.market.limit.flame", 20,
      "server.market.limit.mvip", 15,
      "server.market.limit.svip", 10,
      "server.market.limit.vip", 5,
      "server.market.limit.gracz", 3
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

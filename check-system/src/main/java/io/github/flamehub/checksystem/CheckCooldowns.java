package io.github.flamehub.checksystem;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.bukkit.entity.Player;

public final class CheckCooldowns {

  private final static Map<String, Long> STRING_LONG_MAP = Map.of(
      "check.cooldown.helper", TimeUnit.MINUTES.toMillis(3),
      "check.cooldown.srhelper", TimeUnit.MINUTES.toMillis(2) + TimeUnit.SECONDS.toMillis(30),
      "check.cooldown.jrmod", TimeUnit.MINUTES.toMillis(2),
      "check.cooldown.unlimited", 0L
  );

  public static long getCooldown(Player player) {
    long cooldown = 0;
    for (Map.Entry<String, Long> entry : STRING_LONG_MAP.entrySet()) {
      String key = entry.getKey();
      if (player.hasPermission(key)) {
        Long value = entry.getValue();
        if (value < cooldown) {
          cooldown = value;
        }

      }

    }

    return cooldown;
  }

}

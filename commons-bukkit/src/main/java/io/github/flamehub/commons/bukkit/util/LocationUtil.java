package io.github.flamehub.commons.bukkit.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

public final class LocationUtil {

  private LocationUtil() {

  }

  public static String serialize(@Nullable Location location) {
    if (location == null) {
      return null;
    }

    return location.getWorld().getName() + ":" + location.getX() + ":" + location.getY() + ":"
        + location.getZ() + ":" + location.getYaw() + ":" + location.getPitch();
  }

  public static Location deserialize(String locationFromText) {
    String[] split = locationFromText.split(":");
    return new Location(
        Bukkit.getWorld(split[0]),
        Double.parseDouble(split[1]),
        Double.parseDouble(split[2]),
        Double.parseDouble(split[3]),
        Float.parseFloat(split[4]),
        Float.parseFloat(split[5])
    );
  }

  public static double distance(Location first, Location second) {
    return Math.max(Math.abs(first.getX() - second.getX()), Math.abs(first.getZ() - second.getZ()));
  }

}

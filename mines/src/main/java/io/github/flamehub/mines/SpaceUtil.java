package io.github.flamehub.mines;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

public class SpaceUtil {

  public static List<Location> getSphere(final Location loc, final int radius, final int height, final boolean hollow,
      final boolean sphere, final int plusY) {
    final List<Location> circleblocks = new ArrayList<>();
    final int cx = loc.getBlockX();
    final int cy = loc.getBlockY();
    final int cz = loc.getBlockZ();
    for (int x = cx - radius; x <= cx + radius; x++) {
      for (int z = cz - radius; z <= cz + radius; ) {
        int y = sphere ? (cy - radius) : cy;
        for (; ; z++) {
          if (y < (sphere ? (cy + radius) : (cy + height))) {
            final double dist = ((cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy
                - y)) : 0));
            if (dist < (radius * radius) && (!hollow || dist >= ((radius - 1) * (radius - 1)))) {
              final Location l = new Location(loc.getWorld(), x, (y + plusY), z);
              circleblocks.add(l);
            }
            y++;
            continue;
          }
        }
      }
    }
    return circleblocks;
  }

  public static List<Location> getSquare(final Location center, final int radius) {
    final List<Location> locs = new ArrayList<>();
    final int cX = center.getBlockX();
    final int cZ = center.getBlockZ();
    final int minX = Math.min(cX + radius, cX - radius);
    final int maxX = Math.max(cX + radius, cX - radius);
    final int minZ = Math.min(cZ + radius, cZ - radius);
    final int maxZ = Math.max(cZ + radius, cZ - radius);
    for (int x = minX; x <= maxX; x++) {
      for (int z = minZ; z <= maxZ; z++) {
        locs.add(new Location(center.getWorld(), x, center.getBlockY(), z));
      }
    }
    locs.add(center);
    return locs;
  }

  public static List<Location> getCorners(final Location center, final int radius) {
    final List<Location> locs = new ArrayList<>();
    final int cX = center.getBlockX();
    final int cZ = center.getBlockZ();
    final int minX = Math.min(cX + radius, cX - radius);
    final int maxX = Math.max(cX + radius, cX - radius);
    final int minZ = Math.min(cZ + radius, cZ - radius);
    final int maxZ = Math.max(cZ + radius, cZ - radius);
    locs.add(new Location(center.getWorld(), minX, center.getBlockY(), minZ));
    locs.add(new Location(center.getWorld(), maxX, center.getBlockY(), minZ));
    locs.add(new Location(center.getWorld(), minX, center.getBlockY(), maxZ));
    locs.add(new Location(center.getWorld(), maxX, center.getBlockY(), maxZ));
    return locs;
  }

  public static List<Location> getWalls(final Location center, final int radius) {
    final List<Location> locs = getSquare(center, radius);
    locs.removeAll(getSquare(center, radius - 1));
    return locs;
  }

  public static List<Location> getWalls(final Location center, final int radius, final int height) {
    final List<Location> locs = getWalls(center, radius);
    for (int i = 1; i <= height; i++) {
      locs.addAll(getWalls(
          new Location(center.getWorld(), center.getBlockX(), (center.getBlockY() + i),
              center.getBlockZ()), radius));
    }
    return locs;
  }

  public static List<Location> getSquare(final Location center, final int radius, final int height) {
    final List<Location> locs = getSquare(center, radius);
    for (int i = 1; i <= height; i++) {
      locs.addAll(getSquare(
          new Location(center.getWorld(), center.getBlockX(), (center.getBlockY() + i),
              center.getBlockZ()), radius));
    }
    return locs;
  }

  public static List<Location> getCorners(final Location center, final int radius, final int height) {
    final List<Location> locs = getCorners(center, radius);
    for (int i = 1; i <= height; i++) {
      locs.addAll(getCorners(
          new Location(center.getWorld(), center.getBlockX(), (center.getBlockY() + i),
              center.getBlockZ()), radius));
    }
    return locs;
  }

  public static List<Location> getCircle(final Location center, final double radius, final int amount) {
    final World world = center.getWorld();
    final double increment = 6.283185307179586D / amount;
    final List<Location> locations = new ArrayList<>();
    for (int i = 0; i < amount; i++) {
      final double angle = i * increment;
      final double x = center.getX() + radius * Math.cos(angle);
      final double z = center.getZ() + radius * Math.sin(angle);
      locations.add(new Location(world, x, center.getY(), z));
    }
    return locations;
  }

  public static List<Location> getWall(final Location center, final int radius, final int height,
      final boolean northOrSouth) {
    final List<Location> centerLoc = new ArrayList<>();
    final List<Location> locations = new ArrayList<>();
    centerLoc.add(center);
    int i;
    for (i = 0; i < radius; i++) {
      centerLoc.add(
          center.getBlock().getRelative(northOrSouth ? BlockFace.WEST : BlockFace.NORTH, i + 1)
              .getLocation());
      centerLoc.add(
          center.getBlock().getRelative(northOrSouth ? BlockFace.EAST : BlockFace.SOUTH, i + 1)
              .getLocation());
    }
    if (height > 1) {
      for (i = 0; i < height - 1; i++) {
        for (final Location location : centerLoc) {
          locations.add(location.clone().add(0.0D, (i + 1), 0.0D));
        }
      }
    }
    locations.addAll(centerLoc);
    return locations;
  }
}
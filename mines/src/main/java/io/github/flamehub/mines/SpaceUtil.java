package io.github.flamehub.mines;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;

public class SpaceUtil {
  public static List<Location> getSphere(Location loc, int radius, int height, boolean hollow, boolean sphere, int plusY) {
    List<Location> circleblocks = new ArrayList<>();
    int cx = loc.getBlockX();
    int cy = loc.getBlockY();
    int cz = loc.getBlockZ();
    for (int x = cx - radius; x <= cx + radius; x++) {
      for (int z = cz - radius; z <= cz + radius; ) {
        int y = sphere ? (cy - radius) : cy;
        for (;; z++) {
          if (y < (sphere ? (cy + radius) : (cy + height))) {
            double dist = ((cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy - y)) : 0));
            if (dist < (radius * radius) && (!hollow || dist >= ((radius - 1) * (radius - 1)))) {
              Location l = new Location(loc.getWorld(), x, (y + plusY), z);
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
  
  public static List<Location> getSquare(Location center, int radius) {
    List<Location> locs = new ArrayList<>();
    int cX = center.getBlockX();
    int cZ = center.getBlockZ();
    int minX = Math.min(cX + radius, cX - radius);
    int maxX = Math.max(cX + radius, cX - radius);
    int minZ = Math.min(cZ + radius, cZ - radius);
    int maxZ = Math.max(cZ + radius, cZ - radius);
    for (int x = minX; x <= maxX; x++) {
      for (int z = minZ; z <= maxZ; z++)
        locs.add(new Location(center.getWorld(), x, center.getBlockY(), z)); 
    } 
    locs.add(center);
    return locs;
  }
  
  public static List<Location> getCorners(Location center, int radius) {
    List<Location> locs = new ArrayList<>();
    int cX = center.getBlockX();
    int cZ = center.getBlockZ();
    int minX = Math.min(cX + radius, cX - radius);
    int maxX = Math.max(cX + radius, cX - radius);
    int minZ = Math.min(cZ + radius, cZ - radius);
    int maxZ = Math.max(cZ + radius, cZ - radius);
    locs.add(new Location(center.getWorld(), minX, center.getBlockY(), minZ));
    locs.add(new Location(center.getWorld(), maxX, center.getBlockY(), minZ));
    locs.add(new Location(center.getWorld(), minX, center.getBlockY(), maxZ));
    locs.add(new Location(center.getWorld(), maxX, center.getBlockY(), maxZ));
    return locs;
  }
  
  public static List<Location> getWalls(Location center, int radius) {
    List<Location> locs = getSquare(center, radius);
    locs.removeAll(getSquare(center, radius - 1));
    return locs;
  }
  
  public static List<Location> getWalls(Location center, int radius, int height) {
    List<Location> locs = getWalls(center, radius);
    for (int i = 1; i <= height; i++)
      locs.addAll(getWalls(new Location(center.getWorld(), center.getBlockX(), (center.getBlockY() + i), center.getBlockZ()), radius)); 
    return locs;
  }
  
  public static List<Location> getSquare(Location center, int radius, int height) {
    List<Location> locs = getSquare(center, radius);
    for (int i = 1; i <= height; i++)
      locs.addAll(getSquare(new Location(center.getWorld(), center.getBlockX(), (center.getBlockY() + i), center.getBlockZ()), radius)); 
    return locs;
  }
  
  public static List<Location> getCorners(Location center, int radius, int height) {
    List<Location> locs = getCorners(center, radius);
    for (int i = 1; i <= height; i++)
      locs.addAll(getCorners(new Location(center.getWorld(), center.getBlockX(), (center.getBlockY() + i), center.getBlockZ()), radius)); 
    return locs;
  }
  
  public static List<Location> getCircle(Location center, double radius, int amount) {
    World world = center.getWorld();
    double increment = 6.283185307179586D / amount;
    List<Location> locations = new ArrayList<>();
    for (int i = 0; i < amount; i++) {
      double angle = i * increment;
      double x = center.getX() + radius * Math.cos(angle);
      double z = center.getZ() + radius * Math.sin(angle);
      locations.add(new Location(world, x, center.getY(), z));
    } 
    return locations;
  }
  
  public static List<Location> getWall(Location center, int radius, int height, boolean northOrSouth) {
    List<Location> centerLoc = new ArrayList<>();
    List<Location> locations = new ArrayList<>();
    centerLoc.add(center);
    int i;
    for (i = 0; i < radius; i++) {
      centerLoc.add(center.getBlock().getRelative(northOrSouth ? BlockFace.WEST : BlockFace.NORTH, i + 1).getLocation());
      centerLoc.add(center.getBlock().getRelative(northOrSouth ? BlockFace.EAST : BlockFace.SOUTH, i + 1).getLocation());
    } 
    if (height > 1)
      for (i = 0; i < height - 1; i++) {
        for (Location location : centerLoc)
          locations.add(location.clone().add(0.0D, (i + 1), 0.0D)); 
      }  
    locations.addAll(centerLoc);
    return locations;
  }
}
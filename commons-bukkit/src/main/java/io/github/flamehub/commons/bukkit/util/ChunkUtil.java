package io.github.flamehub.commons.bukkit.util;

import org.bukkit.Location;

public final class ChunkUtil {

  public static long coordinatesToLong(int x, int y, int z) {
    long lx = (x & 0x1FFFFF);
    long ly = (y & 0x1FFFFF);
    long lz = (z & 0x1FFFFF);
    return (lx << 42) | (ly << 21) | lz;
  }

  public static int[] longToCoordinates(long l) {
    int x = (int) ((l >> 42) & 0x1FFFFF); // 21 bitów na X
    int y = (int) ((l >> 21) & 0x1FFFFF); // 21 bitów na Y
    int z = (int) (l & 0x1FFFFF); // 21 bitów na Z
    return new int[]{x, y, z};
  }

  public static long locationToChunkKey(Location location) {
    return coordinatesToLong(location.getBlockX() >> 4, location.getBlockZ() >> 4);
  }

  public static long coordinatesToLong(int x, int z) {
    return ((long) x << 32) | (z & 0xffffffffL);
  }

  public static int extractXFromLong(long combined) {
    return (int) (combined >> 32);
  }

  public static int extractZFromLong(long combined) {
    return (int) (combined & 0xffffffffL);
  }

  public static int toChunkRelativeCoordinates(int blockCoordinate, int chunkCoordinate) {
    return blockCoordinate - (chunkCoordinate << 4);
  }

  public static int toBlockCoordinate(int chunkRelativeCoordinate, int chunkCoordinate) {
    return chunkRelativeCoordinate + (chunkCoordinate << 4);
  }

}

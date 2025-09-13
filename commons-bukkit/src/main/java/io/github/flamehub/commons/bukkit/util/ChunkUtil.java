package io.github.flamehub.commons.bukkit.util;

import org.bukkit.Location;

public final class ChunkUtil {

  public static long coordinatesToLong(final int x, final int y, final int z) {
    final long lx = (x & 0x1FFFFF);
    final long ly = (y & 0x1FFFFF);
    final long lz = (z & 0x1FFFFF);
    return (lx << 42) | (ly << 21) | lz;
  }

  public static int[] longToCoordinates(final long l) {
    final int x = (int) ((l >> 42) & 0x1FFFFF); // 21 bitów na X
    final int y = (int) ((l >> 21) & 0x1FFFFF); // 21 bitów na Y
    final int z = (int) (l & 0x1FFFFF); // 21 bitów na Z
    return new int[]{x, y, z};
  }

  public static long locationToChunkKey(final Location location) {
    return coordinatesToLong(location.getBlockX() >> 4, location.getBlockZ() >> 4);
  }

  public static long coordinatesToLong(final int x, final int z) {
    return ((long) x << 32) | (z & 0xffffffffL);
  }

  public static int extractXFromLong(final long combined) {
    return (int) (combined >> 32);
  }

  public static int extractZFromLong(final long combined) {
    return (int) (combined & 0xffffffffL);
  }

  public static int toChunkRelativeCoordinates(final int blockCoordinate, final int chunkCoordinate) {
    return blockCoordinate - (chunkCoordinate << 4);
  }

  public static int toBlockCoordinate(final int chunkRelativeCoordinate, final int chunkCoordinate) {
    return chunkRelativeCoordinate + (chunkCoordinate << 4);
  }

}

package io.github.flamehub.commons.bukkit.util;

import org.bukkit.Location;

public final class ChunkUtil {

    public static long locationToChunkKey(Location location){
        return coordinatesToLong(location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

    public static long coordinatesToLong(int x, int z){
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

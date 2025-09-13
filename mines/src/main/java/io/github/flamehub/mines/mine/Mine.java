package io.github.flamehub.mines.mine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.decentsoftware.holograms.api.DHAPI;
import java.io.Serializable;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public final class Mine implements Serializable {

  private String id;
  private String renewDelay;
  private long totalBlocks;

  @JsonIgnore
  private long lastTimeGenerate;

  private List<MineBlock> spawningBlocks;

  private Location firstLocation;
  private Location secondLocation;

  public Mine() {
  }

  public Mine(
      final String id,
      final String renewDelay,
      final List<MineBlock> spawningBlocks,
      final Location firstLocation,
      final Location secondLocation
  ) {
    this.id = id;
    this.renewDelay = renewDelay;
    this.spawningBlocks = spawningBlocks;
    this.firstLocation = firstLocation;
    this.secondLocation = secondLocation;

  }

  public static Location calculateCenter(final Location min, final Location max) {

    final int minX = Math.min(min.getBlockX(), max.getBlockX());
    final int minY = Math.min(min.getBlockY(), max.getBlockY());
    final int minZ = Math.min(min.getBlockZ(), max.getBlockZ());
    final int maxX = Math.max(min.getBlockX(), max.getBlockX());
    final int maxY = Math.max(min.getBlockY(), max.getBlockY());
    final int maxZ = Math.max(min.getBlockZ(), max.getBlockZ());

    min.setX(minX);
    min.setY(minY);
    min.setZ(minZ);
    max.setX(maxX);
    max.setY(maxY);
    max.setZ(maxZ);

    final double centerX = (min.getX() + max.getX()) / 2;
    final double centerY = (min.getY() + max.getY()) / 2;
    final double centerZ = (min.getZ() + max.getZ()) / 2;

    return new Location(min.getWorld(), centerX, centerY, centerZ).toCenterLocation();
  }

  public void createHolo() {
    if (DHAPI.getHologram(id) == null) {
      DHAPI.createHologram(id, calculateCenter(firstLocation.clone(), secondLocation.clone()),
          false, List.of("&6⏳ &fʀᴇɢᴇɴᴇʀᴀᴄᴊᴀ ᴢᴀ: &e%mines_" + id + "%"));
    }
  }

  public void regenerate() {
    final World world = firstLocation.getWorld();
    final int x1 = firstLocation.getBlockX();
    final int y1 = firstLocation.getBlockY();
    final int z1 = firstLocation.getBlockZ();

    final int x2 = secondLocation.getBlockX();
    final int y2 = secondLocation.getBlockY();
    final int z2 = secondLocation.getBlockZ();

    final int minX = Math.min(x1, x2);
    final int minY = Math.min(y1, y2);
    final int minZ = Math.min(z1, z2);

    final int maxX = Math.max(x1, x2);
    final int maxY = Math.max(y1, y2);
    final int maxZ = Math.max(z1, z2);

    double totalChance = 0;
    for (final MineBlock spawningBlock : spawningBlocks) {
      totalChance += spawningBlock.getChance();
    }

    for (int x = minX; x <= maxX; x++) {
      for (int y = minY; y <= maxY; y++) {
        for (int z = minZ; z <= maxZ; z++) {
          final Location blockLocation = new Location(world, x, y, z);
          final Block block = blockLocation.getBlock();

          final double randomValue = Math.random() * totalChance;

          double cumulativeChance = 0;
          for (final MineBlock spawningBlock : spawningBlocks) {
            cumulativeChance += spawningBlock.getChance();
            if (randomValue <= cumulativeChance) {
              block.setType(spawningBlock.getMaterial());
              break;
            }
          }
        }
      }
    }
  }


  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public String getRenewDelay() {
    return renewDelay;
  }

  public void setRenewDelay(final String renewDelay) {
    this.renewDelay = renewDelay;
  }

  public long getLastTimeGenerate() {
    return lastTimeGenerate;
  }

  public void setLastTimeGenerate(final long lastTimeGenerate) {
    this.lastTimeGenerate = lastTimeGenerate;
  }

  public List<MineBlock> getSpawningBlocks() {
    return spawningBlocks;
  }

  public void setSpawningBlocks(final List<MineBlock> spawningBlocks) {
    this.spawningBlocks = spawningBlocks;
  }

  public Location getFirstLocation() {
    return firstLocation;
  }

  public void setFirstLocation(final Location firstLocation) {
    this.firstLocation = firstLocation;
  }

  public Location getSecondLocation() {
    return secondLocation;
  }

  public void setSecondLocation(final Location secondLocation) {
    this.secondLocation = secondLocation;
  }

  public long getTotalBlocks() {
    return totalBlocks;
  }

  public void setTotalBlocks(final long totalBlocks) {
    this.totalBlocks = totalBlocks;
  }
}

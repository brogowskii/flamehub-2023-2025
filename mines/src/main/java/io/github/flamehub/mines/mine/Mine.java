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

  public Mine(String id, String renewDelay, List<MineBlock> spawningBlocks, Location firstLocation,
      Location secondLocation) {
    this.id = id;
    this.renewDelay = renewDelay;
    this.spawningBlocks = spawningBlocks;
    this.firstLocation = firstLocation;
    this.secondLocation = secondLocation;

  }

  public static Location calculateCenter(Location min, Location max) {

    int minX = Math.min(min.getBlockX(), max.getBlockX());
    int minY = Math.min(min.getBlockY(), max.getBlockY());
    int minZ = Math.min(min.getBlockZ(), max.getBlockZ());
    int maxX = Math.max(min.getBlockX(), max.getBlockX());
    int maxY = Math.max(min.getBlockY(), max.getBlockY());
    int maxZ = Math.max(min.getBlockZ(), max.getBlockZ());

    min.setX(minX);
    min.setY(minY);
    min.setZ(minZ);
    max.setX(maxX);
    max.setY(maxY);
    max.setZ(maxZ);

    double centerX = (min.getX() + max.getX()) / 2;
    double centerY = (min.getY() + max.getY()) / 2;
    double centerZ = (min.getZ() + max.getZ()) / 2;

    return new Location(min.getWorld(), centerX, centerY, centerZ).toCenterLocation();
  }

  public void createHolo() {
    if (DHAPI.getHologram(id) == null) {
      DHAPI.createHologram(id, calculateCenter(firstLocation.clone(), secondLocation.clone()),
          false, List.of("&6⏳ &fʀᴇɢᴇɴᴇʀᴀᴄᴊᴀ ᴢᴀ: &e%mines_" + id + "%"));
    }
  }

  public void regenerate() {
    World world = firstLocation.getWorld();
    int x1 = firstLocation.getBlockX();
    int y1 = firstLocation.getBlockY();
    int z1 = firstLocation.getBlockZ();

    int x2 = secondLocation.getBlockX();
    int y2 = secondLocation.getBlockY();
    int z2 = secondLocation.getBlockZ();

    int minX = Math.min(x1, x2);
    int minY = Math.min(y1, y2);
    int minZ = Math.min(z1, z2);

    int maxX = Math.max(x1, x2);
    int maxY = Math.max(y1, y2);
    int maxZ = Math.max(z1, z2);

    double totalChance = 0;
    for (MineBlock spawningBlock : spawningBlocks) {
      totalChance += spawningBlock.getChance();
    }

    for (int x = minX; x <= maxX; x++) {
      for (int y = minY; y <= maxY; y++) {
        for (int z = minZ; z <= maxZ; z++) {
          Location blockLocation = new Location(world, x, y, z);
          Block block = blockLocation.getBlock();

          double randomValue = Math.random() * totalChance;

          double cumulativeChance = 0;
          for (MineBlock spawningBlock : spawningBlocks) {
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

  public void setId(String id) {
    this.id = id;
  }

  public String getRenewDelay() {
    return renewDelay;
  }

  public void setRenewDelay(String renewDelay) {
    this.renewDelay = renewDelay;
  }

  public long getLastTimeGenerate() {
    return lastTimeGenerate;
  }

  public void setLastTimeGenerate(long lastTimeGenerate) {
    this.lastTimeGenerate = lastTimeGenerate;
  }

  public List<MineBlock> getSpawningBlocks() {
    return spawningBlocks;
  }

  public void setSpawningBlocks(List<MineBlock> spawningBlocks) {
    this.spawningBlocks = spawningBlocks;
  }

  public Location getFirstLocation() {
    return firstLocation;
  }

  public void setFirstLocation(Location firstLocation) {
    this.firstLocation = firstLocation;
  }

  public Location getSecondLocation() {
    return secondLocation;
  }

  public void setSecondLocation(Location secondLocation) {
    this.secondLocation = secondLocation;
  }

  public long getTotalBlocks() {
    return totalBlocks;
  }

  public void setTotalBlocks(long totalBlocks) {
    this.totalBlocks = totalBlocks;
  }
}

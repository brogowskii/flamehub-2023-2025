package io.github.flamehub.player.sync;

import io.github.flamehub.commons.config.FlameConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public final class PlayerSyncConfig extends FlameConfig {

  private Location spawnLocation = new Location(Bukkit.getWorld("world"), 0.5, 100, 0.5);


  public PlayerSyncConfig() {
  }

  public Location getSpawnLocation() {
    return spawnLocation;
  }

  public void setSpawnLocation(Location spawnLocation) {
    this.spawnLocation = spawnLocation;
  }
}

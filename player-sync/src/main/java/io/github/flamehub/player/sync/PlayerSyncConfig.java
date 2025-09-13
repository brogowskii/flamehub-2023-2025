package io.github.flamehub.player.sync;

import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@FlameConfigProperties(name = "player-sync.json")
public final class PlayerSyncConfig extends FlameConfig {

  private Location spawnLocation = new Location(Bukkit.getWorld("world"), 0.5, 70, 0.5, 90, 0);

  public PlayerSyncConfig() {
  }

  public Location getSpawnLocation() {
    return spawnLocation;
  }

  public void setSpawnLocation(final Location spawnLocation) {
    this.spawnLocation = spawnLocation;
  }
}

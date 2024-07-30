package io.github.flamehub.essentials.spawn;

import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import org.bukkit.Location;

@FlameConfigProperties(name = "spawn.json")
public final class SpawnConfig extends FlameConfig {

  private Location spawnLocation;

  public SpawnConfig() {
  }

  public Location getSpawnLocation() {
    return spawnLocation;
  }

  public void setSpawnLocation(Location spawnLocation) {
    this.spawnLocation = spawnLocation;
  }
}

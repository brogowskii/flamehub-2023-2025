package io.github.flamehub.essentials.spawn;

import io.github.flamehub.commons.config.FlameConfigService;
import org.bukkit.Location;

public final class SpawnFacade {

  private final SpawnConfig spawnConfig;

  SpawnFacade(final SpawnConfig spawnConfig) {
    this.spawnConfig = spawnConfig;
  }

  public Location getSpawnLocation() {
    return this.spawnConfig.getSpawnLocation();
  }

  public void setSpawnLocation(final Location spawnLocation) {
    this.spawnConfig.setSpawnLocation(spawnLocation);
  }

  void saveConfig(FlameConfigService flameConfigService) {
    flameConfigService.saveLocally(SpawnConfig.class);
  }

  void refreshConfig(final FlameConfigService flameConfigService) throws IllegalAccessException {
    flameConfigService.refreshLocally(SpawnConfig.class);
  }

}

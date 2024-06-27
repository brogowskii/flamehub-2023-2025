package io.github.flamehub.essentials.spawn;

import io.github.flamehub.commons.legacy.config.MongoConfigService;
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

    void saveConfig(MongoConfigService mongoConfigService) {
        mongoConfigService.save(this.spawnConfig);
    }

    void refreshConfig(final MongoConfigService mongoConfigService) throws IllegalAccessException {
        mongoConfigService.refresh(SpawnConfig.class, this.spawnConfig);
    }

}

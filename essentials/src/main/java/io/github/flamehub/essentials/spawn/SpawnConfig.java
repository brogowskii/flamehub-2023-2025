package io.github.flamehub.essentials.spawn;

import io.github.flamehub.commons.config.MongoConfig;
import org.bukkit.Location;

final class SpawnConfig extends MongoConfig {

    private Location spawnLocation;

    public SpawnConfig() {
    }

    public SpawnConfig(String id) {
        super(id);
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }
}

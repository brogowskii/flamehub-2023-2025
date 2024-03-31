package io.github.flamehub.player.sync;

import io.github.flamehub.commons.config.MongoConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public final class PlayerSyncConfig extends MongoConfig {

    private Location spawnLocation = new Location(Bukkit.getWorld("world"), 0.5, 100, 0.5);


    public PlayerSyncConfig() {
    }

    public PlayerSyncConfig(String id) {
        super(id);
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }
}

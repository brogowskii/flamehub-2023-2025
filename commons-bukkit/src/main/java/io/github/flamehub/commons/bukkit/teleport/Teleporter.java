package io.github.flamehub.commons.bukkit.teleport;

import org.bukkit.Location;

import java.time.Instant;
import java.util.UUID;

public final class Teleporter {

    private final UUID uniqueId;
    private final Location startLocation;
    private final Location targetLocation;
    private final Instant teleportTime;

    public Teleporter(UUID uniqueId, Location startLocation, Location targetLocation, Instant teleportTime) {
        this.uniqueId = uniqueId;
        this.startLocation = startLocation;
        this.targetLocation = targetLocation;
        this.teleportTime = teleportTime;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    public Instant getTeleportTime() {
        return teleportTime;
    }
}

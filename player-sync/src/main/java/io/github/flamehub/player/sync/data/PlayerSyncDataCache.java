package io.github.flamehub.player.sync.data;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerSyncDataCache {

    private final Map<UUID, PlayerSyncData> playerSyncDataCache = new ConcurrentHashMap<>();

    public void add(PlayerSyncData playerSyncData) {
        this.playerSyncDataCache.put(playerSyncData.getPlayerUniqueId(), playerSyncData);
    }

    public void remove(UUID uuid) {
        this.playerSyncDataCache.remove(uuid);
    }

    public Optional<PlayerSyncData> findByUniqueId(UUID playerUniqueId) {
        return Optional.ofNullable(this.playerSyncDataCache.get(playerUniqueId));
    }

}

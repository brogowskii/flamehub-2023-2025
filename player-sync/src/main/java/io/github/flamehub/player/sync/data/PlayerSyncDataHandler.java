package io.github.flamehub.player.sync.data;

import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.messenger.packet.PacketHandler;

public final class PlayerSyncDataHandler {

    private final RedisMessenger redisMessenger;
    private final PlayerSyncDataCache playerSyncDataCache;

    public PlayerSyncDataHandler(RedisMessenger redisMessenger, PlayerSyncDataCache playerSyncDataCache) {
        this.redisMessenger = redisMessenger;
        this.playerSyncDataCache = playerSyncDataCache;
    }

    @PacketHandler
    public void handle(PlayerSyncDataRequest request) {
        this.playerSyncDataCache.add(request.getPlayerSyncData());
        this.redisMessenger.publish("callbacks", new PlayerSyncDataResponse(request.getUniqueId()));
    }

}

package io.github.flamehub.commons.network.player;

import io.github.flamehub.commons.json.JsonUtil;
import io.github.flamehub.commons.messenger.packet.PacketHandler;

public final class NetworkPlayerHandler {

    private final NetworkPlayerCache networkPlayerCache;

    public NetworkPlayerHandler(NetworkPlayerCache networkPlayerCache) {
        this.networkPlayerCache = networkPlayerCache;
    }

    @PacketHandler
    public void handle(NetworkPlayerUpdate update) {
        NetworkPlayer networkPlayer = JsonUtil.DATABASE_GSON.fromJson(update.getNetworkPlayerJson(), NetworkPlayer.class);
        this.networkPlayerCache.add(networkPlayer);
    }

    @PacketHandler
    public void handle(NetworkPlayerDelete update) {
        NetworkPlayer networkPlayer = this.networkPlayerCache.findByUniqueId(update.getNetworkPlayerUniqueId());
        if (networkPlayer == null) {
            return;
        }

        this.networkPlayerCache.remove(networkPlayer);
    }

}

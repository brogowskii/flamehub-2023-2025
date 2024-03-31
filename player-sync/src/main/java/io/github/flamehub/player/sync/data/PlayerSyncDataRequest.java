package io.github.flamehub.player.sync.data;

import io.github.flamehub.commons.messenger.packet.PacketRequest;

public final class PlayerSyncDataRequest extends PacketRequest {

    private final PlayerSyncData playerSyncData;

    public PlayerSyncDataRequest(PlayerSyncData playerSyncData) {
        this.playerSyncData = playerSyncData;
    }

    public PlayerSyncData getPlayerSyncData() {
        return playerSyncData;
    }
}

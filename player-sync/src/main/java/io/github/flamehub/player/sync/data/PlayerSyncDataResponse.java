package io.github.flamehub.player.sync.data;

import io.github.flamehub.commons.messenger.packet.PacketResponse;

import java.util.UUID;

public final class PlayerSyncDataResponse extends PacketResponse {
    public PlayerSyncDataResponse(UUID uniqueId) {
        super(uniqueId);
    }
}

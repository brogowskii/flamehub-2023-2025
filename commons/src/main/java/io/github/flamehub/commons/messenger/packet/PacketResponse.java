package io.github.flamehub.commons.messenger.packet;

import java.util.UUID;

public class PacketResponse implements Packet {

    private final UUID uniqueId;

    public PacketResponse(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

}

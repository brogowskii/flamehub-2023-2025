package io.github.flamehub.commons.punishment;

import io.github.flamehub.commons.messenger.packet.Packet;

public final class PunishmentKickPacket implements Packet {

    private final String player;
    private final String reason;

    public PunishmentKickPacket(String player, String reason) {
        this.player = player;
        this.reason = reason;
    }

    public String getPlayer() {
        return player;
    }

    public String getReason() {
        return reason;
    }
}

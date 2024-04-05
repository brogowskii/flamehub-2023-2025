package io.github.flamehub.economy.user;

import io.github.flamehub.commons.messenger.packet.Packet;

import java.util.UUID;

public final class EconomyUserUpdate implements Packet {

    private final UUID uniqueId;
    private final double money;

    public EconomyUserUpdate(final UUID uniqueId, final double money) {
        this.uniqueId = uniqueId;
        this.money = money;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public double getMoney() {
        return money;
    }
}

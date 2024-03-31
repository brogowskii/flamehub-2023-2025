package io.github.flamehub.wallet.user.update;

import io.github.flamehub.commons.messenger.packet.Packet;

import java.util.UUID;

public final class WalletUserUpdate implements Packet {

    private final UUID uniqueId;
    private final double newMoney;

    public WalletUserUpdate(UUID uniqueId, double newMoney) {
        this.uniqueId = uniqueId;
        this.newMoney = newMoney;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public double getNewMoney() {
        return newMoney;
    }
}

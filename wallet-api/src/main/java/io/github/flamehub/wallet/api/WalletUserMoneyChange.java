package io.github.flamehub.wallet.api;

import io.github.flamehub.commons.messenger.packet.Packet;

public final class WalletUserMoneyChange implements Packet {

    private final String name;
    private final double value;

    public WalletUserMoneyChange(String name, double value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }
}

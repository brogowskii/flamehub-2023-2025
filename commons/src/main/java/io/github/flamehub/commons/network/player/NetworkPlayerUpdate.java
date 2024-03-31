package io.github.flamehub.commons.network.player;

import io.github.flamehub.commons.messenger.packet.Packet;

public final class NetworkPlayerUpdate implements Packet {

    private final String networkPlayerJson;

    public NetworkPlayerUpdate(String networkPlayerJson) {
        this.networkPlayerJson = networkPlayerJson;
    }

    public String getNetworkPlayerJson() {
        return networkPlayerJson;
    }
}

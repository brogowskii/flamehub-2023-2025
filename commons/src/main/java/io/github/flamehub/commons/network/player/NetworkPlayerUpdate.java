package io.github.flamehub.commons.network.player;

import io.github.flamehub.commons.messenger.packet.Packet;

public final class NetworkPlayerUpdate implements Packet {

  private NetworkPlayer networkPlayer;

  public NetworkPlayerUpdate(final NetworkPlayer networkPlayer) {
    this.networkPlayer = networkPlayer;
  }

  public NetworkPlayer getNetworkPlayer() {
    return networkPlayer;
  }
}

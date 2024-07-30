package io.github.flamehub.commons.network.player;

import io.github.flamehub.commons.messenger.packet.Packet;
import java.util.UUID;

public final class NetworkPlayerDelete implements Packet {

  private UUID networkPlayerUniqueId;

  public NetworkPlayerDelete() {
  }

  public NetworkPlayerDelete(UUID networkPlayerUniqueId) {
    this.networkPlayerUniqueId = networkPlayerUniqueId;
  }

  public UUID getNetworkPlayerUniqueId() {
    return networkPlayerUniqueId;
  }
}

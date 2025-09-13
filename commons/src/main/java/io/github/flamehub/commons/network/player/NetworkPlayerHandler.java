package io.github.flamehub.commons.network.player;

import io.github.flamehub.commons.messenger.packet.PacketHandler;

public final class NetworkPlayerHandler {

  private final NetworkPlayerCache networkPlayerCache;

  public NetworkPlayerHandler(final NetworkPlayerCache networkPlayerCache) {
    this.networkPlayerCache = networkPlayerCache;
  }

  @PacketHandler
  public void handle(final NetworkPlayerUpdate update) {
    networkPlayerCache.add(update.getNetworkPlayer());
  }

  @PacketHandler
  public void handle(final NetworkPlayerDelete update) {
    final NetworkPlayer networkPlayer = networkPlayerCache.findByUniqueId(
        update.getNetworkPlayerUniqueId());
    if (networkPlayer == null) {
      return;
    }

    networkPlayerCache.remove(networkPlayer);
  }

}

package io.github.flamehub.commons.server;

import io.github.flamehub.commons.messenger.packet.PacketHandler;
import java.time.Instant;
import java.util.logging.Logger;


public final class NetworkServerUpdateHandler {

  private final Logger logger;
  private final NetworkServerCache networkServerCache;

  public NetworkServerUpdateHandler(Logger logger, NetworkServerCache networkServerCache) {
    this.logger = logger;
    this.networkServerCache = networkServerCache;
  }

  @PacketHandler
  public void handle(NetworkServerUpdate update) {
    networkServerCache.findByName(update.getName()).ifPresent(networkServer -> {
      NetworkServerStatistics statistics = networkServer.getStatistics();
      statistics.setPlayers(update.getPlayers());
      statistics.setPlayersLimit(update.getPlayersLimit());
      statistics.setTps(update.getTps());
      statistics.setLastUpdate(Instant.now());
      statistics.setFrozen(update.isFrozen());
    });

  }

}

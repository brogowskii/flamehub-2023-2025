package io.github.flamehub.proxy.core.server;

import com.velocitypowered.api.proxy.ProxyServer;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerFacade;
import io.github.flamehub.commons.server.NetworkServerStatistics;
import io.github.flamehub.commons.server.NetworkServerUpdater;
import java.time.Instant;

public final class NetworkServerUpdateTask extends NetworkServerUpdater {

  private final ProxyServer proxyServer;

  public NetworkServerUpdateTask(
      final NetworkServerFacade networkServerFacade,
      final ProxyServer proxyServer) {
    super(networkServerFacade);
    this.proxyServer = proxyServer;
  }

  @Override
  public void run() {
    update(networkServer -> {
      final NetworkServerStatistics statistics = networkServer.getStatistics();
      statistics.setLastUpdate(Instant.now());
      statistics.setPlayers(proxyServer.getPlayerCount());
      statistics.setTps(new double[4]);
      return networkServer;
    });
  }
}

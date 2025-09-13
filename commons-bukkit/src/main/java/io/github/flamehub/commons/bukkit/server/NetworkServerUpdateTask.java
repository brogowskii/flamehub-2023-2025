package io.github.flamehub.commons.bukkit.server;

import io.github.flamehub.commons.server.NetworkServerFacade;
import io.github.flamehub.commons.server.NetworkServerStatistics;
import io.github.flamehub.commons.server.NetworkServerUpdater;
import java.time.Instant;
import org.bukkit.Bukkit;

public final class NetworkServerUpdateTask extends NetworkServerUpdater {

  public NetworkServerUpdateTask(final NetworkServerFacade networkServerFacade) {
    super(networkServerFacade);
  }

  @Override
  public void run() {
    update(networkServer -> {
      final NetworkServerStatistics statistics = networkServer.getStatistics();
      statistics.setPlayers(Bukkit.getOnlinePlayers().size());
      statistics.setTps(Bukkit.getTPS());
      statistics.setLastUpdate(Instant.now());
      return networkServer;
    });
  }
}

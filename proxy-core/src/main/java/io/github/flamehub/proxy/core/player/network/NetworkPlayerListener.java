package io.github.flamehub.proxy.core.player.network;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.server.NetworkServerCache;

public final class NetworkPlayerListener {

  private final NetworkServerCache networkServerCache;
  private final NetworkPlayerCache networkPlayerCache;

  public NetworkPlayerListener(
      final NetworkServerCache networkServerCache,
      final NetworkPlayerCache networkPlayerCache) {
    this.networkServerCache = networkServerCache;
    this.networkPlayerCache = networkPlayerCache;
  }

  @Subscribe
  public void onConnect(final ServerConnectedEvent event) {
    final Player player = event.getPlayer();
    final RegisteredServer server = event.getServer();
    final NetworkPlayer networkPlayer = new NetworkPlayer(player.getUniqueId(), player.getUsername());
    networkPlayer.setServer(server.getServerInfo().getName());

    networkServerCache.findByName(server.getServerInfo().getName())
        .ifPresent(networkServer -> {
          networkPlayer.setServerCategory(networkServer.getCategory());
        });

    networkPlayer.setProxy(networkServerCache.getCurrent().getName());
    networkPlayerCache.save(networkPlayer);

  }

  @Subscribe
  public void onDisconnect(final DisconnectEvent event) {
    final Player player = event.getPlayer();
    final NetworkPlayer networkPlayer = networkPlayerCache.findByUniqueId(player.getUniqueId());
    if (networkPlayer == null) {
      return;
    }

    networkPlayerCache.delete(networkPlayer);

  }

}

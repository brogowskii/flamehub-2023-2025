package io.github.flamehub.proxy.core.player.network;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.server.NetworkServerCache;

public class NetworkPlayerListener {

  private final NetworkServerCache networkServerCache;
  private final NetworkPlayerCache networkPlayerCache;

  public NetworkPlayerListener(NetworkServerCache networkServerCache,
      NetworkPlayerCache networkPlayerCache) {
    this.networkServerCache = networkServerCache;
    this.networkPlayerCache = networkPlayerCache;
  }

  @Subscribe
  public void onConnect(ServerConnectedEvent event) {
    Player player = event.getPlayer();
    RegisteredServer server = event.getServer();
    NetworkPlayer networkPlayer = new NetworkPlayer(player.getUniqueId(), player.getUsername());
    networkPlayer.setServer(server.getServerInfo().getName());

    this.networkServerCache.findByName(server.getServerInfo().getName())
        .ifPresent(networkServer -> {
          networkPlayer.setServerCategory(networkServer.getCategory());
        });

    networkPlayer.setProxy(this.networkServerCache.getCurrent().getName());
    this.networkPlayerCache.save(networkPlayer);

  }

  @Subscribe
  public void onDisconnect(DisconnectEvent event) {
    Player player = event.getPlayer();
    NetworkPlayer networkPlayer = this.networkPlayerCache.findByUniqueId(player.getUniqueId());
    if (networkPlayer == null) {
      return;
    }

    this.networkPlayerCache.delete(networkPlayer);

  }

}

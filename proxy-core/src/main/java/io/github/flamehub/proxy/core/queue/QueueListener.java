package io.github.flamehub.proxy.core.queue;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.proxy.core.ProxyCore;
import io.github.flamehub.proxy.core.message.VelocityMessage;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public final class QueueListener {

  private final NetworkServerCache networkServerCache;
  private final QueueService queueService;
  private final ProxyServer proxyServer;
  private final ProxyCore proxyCore;

  public QueueListener(NetworkServerCache networkServerCache, QueueService queueService,
      ProxyServer proxyServer, ProxyCore proxyCore) {
    this.networkServerCache = networkServerCache;
    this.queueService = queueService;
    this.proxyServer = proxyServer;
    this.proxyCore = proxyCore;
  }

  @Subscribe
  public void onQuit(DisconnectEvent event) {
    final Player player = event.getPlayer();
    this.queueService.removeEntryFromAllQueues(player.getUsername());
    System.out.println("usunieto gracza " + player.getUsername() + " z kolejek z poziomu kodu onQuit");
  }

  @Subscribe
  public void onQuit(final KickedFromServerEvent event) {
    if (event.kickedDuringServerConnect()) {
      return;
    }

    final RegisteredServer server = event.getServer();
    final ServerInfo serverInfo = server.getServerInfo();
    if (serverInfo.getName().equals("auth") || serverInfo.getName().equals("queue")) {
      return;
    }

    final Optional<NetworkServer> optionalNetworkServer = networkServerCache.findByName(
        serverInfo.getName());
    optionalNetworkServer.ifPresent(networkServer -> {

      final Player player = event.getPlayer();
      VelocityMessage.from(
          "",
          "&cUtracono połączenie z serwerem &4" + networkServer.getName() + "&c!",
          "&bŁączę z kolejką &3" + networkServer.getCategory() + "&b...",
          ""
      ).send(player);

      KickedFromServerEvent.ServerKickResult kickResult = KickedFromServerEvent.RedirectPlayer.create(
          proxyServer.getServer("queue").get());
      event.setResult(kickResult);
      this.proxyServer.getScheduler()
          .buildTask(this.proxyCore,
              () -> {

                Queue queue = queueService.getOrCreate(networkServer.getCategory());
                queue.addEntry(player.getUsername());

              })
          .delay(4, TimeUnit.SECONDS)
          .schedule();

    });

  }

  @Subscribe
  public void onConnect(final ServerConnectedEvent event) {
    final Optional<RegisteredServer> optionalPreviousServer = event.getPreviousServer();
    if (optionalPreviousServer.isEmpty()) {
      return;
    }

    final RegisteredServer previousServer = optionalPreviousServer.get();
    final ServerInfo serverInfo = previousServer.getServerInfo();
    final Player player = event.getPlayer();
    if (serverInfo.getName().equals("queue")) {
      this.queueService.removeEntryFromAllQueues(player.getUsername());
    }

  }

}

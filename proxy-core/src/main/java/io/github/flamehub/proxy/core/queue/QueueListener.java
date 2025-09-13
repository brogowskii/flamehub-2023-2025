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
import io.github.flamehub.commons.server.NetworkServerFacade;
import io.github.flamehub.proxy.core.ProxyCore;
import io.github.flamehub.proxy.core.message.VelocityMessage;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public final class QueueListener {

  private final NetworkServerFacade networkServerFacade;
  private final QueueService queueService;
  private final ProxyServer proxyServer;
  private final ProxyCore proxyCore;

  public QueueListener(final NetworkServerFacade networkServerFacade, final QueueService queueService,
      final ProxyServer proxyServer, final ProxyCore proxyCore) {
    this.networkServerFacade = networkServerFacade;
    this.queueService = queueService;
    this.proxyServer = proxyServer;
    this.proxyCore = proxyCore;
  }

  @Subscribe
  public void onQuit(final DisconnectEvent event) {
    final Player player = event.getPlayer();
    queueService.removeEntryFromAllQueues(player.getUsername());
  }

  @Subscribe
  public void onQuit(final KickedFromServerEvent event) {
    if (event.kickedDuringServerConnect()) {
      return;
    }

    final RegisteredServer server = event.getServer();
    final ServerInfo serverInfo = server.getServerInfo();
    if ("auth".equals(serverInfo.getName()) || "queue".equals(serverInfo.getName())) {
      return;
    }

    final Optional<NetworkServer> optionalNetworkServer = networkServerFacade.findByName(
        serverInfo.getName());
    optionalNetworkServer.ifPresent(networkServer -> {

      final Player player = event.getPlayer();
      VelocityMessage.from(
          "",
          "&cUtracono połączenie z serwerem &4" + networkServer.getName() + "&c!",
          "&bŁączę z kolejką &3" + networkServer.getCategory() + "&b...",
          ""
      ).deliver(player);

      final KickedFromServerEvent.ServerKickResult kickResult = KickedFromServerEvent.RedirectPlayer.create(
          proxyServer.getServer("queue").get());
      event.setResult(kickResult);
      proxyServer.getScheduler()
          .buildTask(proxyCore,
              () -> {

                final Queue queue = queueService.getOrCreate(networkServer.getCategory());
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
    if ("queue".equals(serverInfo.getName())) {
      queueService.removeEntryFromAllQueues(player.getUsername());
    }

  }

}

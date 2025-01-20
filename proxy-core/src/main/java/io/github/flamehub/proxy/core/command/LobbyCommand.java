package io.github.flamehub.proxy.core.command;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.proxy.core.ProxyMessages;

@Command(name = "lobby", aliases = "hub")
public final class LobbyCommand {

  private final ProxyServer proxyServer;
  private final ProxyMessages proxyMessages;
  private final NetworkServerCache networkServerCache;

  public LobbyCommand(
      final ProxyServer proxyServer,
      final ProxyMessages proxyMessages,
      final NetworkServerCache networkServerCache) {
    this.proxyServer = proxyServer;
    this.proxyMessages = proxyMessages;
    this.networkServerCache = networkServerCache;
  }


  @Execute
  void execute(final @Context Player player) {

    final NetworkServer networkServer = networkServerCache.getLeastCrowded("lobby");
    if (networkServer.isOffline()) {
      proxyMessages
          .cannotFindOnlineLobby
          .deliver(player);
      return;
    }

    final NetworkServer current = networkServerCache.getCurrent();
    if (current.getName().equals(networkServer.getName())) {
      proxyMessages
          .alreadyConnectedToThisServer
          .deliver(player);
      return;
    }

    player.createConnectionRequest(proxyServer.getServer(networkServer.getName()).get())
        .fireAndForget();
  }

}

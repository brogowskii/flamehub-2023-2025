package io.github.flamehub.proxy.core.command;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.proxy.core.auth.user.AuthUserCache;
import io.github.flamehub.proxy.core.message.VelocityMessagesService;

@Command(name = "lobby", aliases = "hub")
public final class LobbyCommand {

  private final ProxyServer proxyServer;
  private final NetworkServerCache networkServerCache;
  private final VelocityMessagesService messagesService;
  private final AuthUserCache authUserCache;

  public LobbyCommand(ProxyServer proxyServer, NetworkServerCache networkServerCache,
      VelocityMessagesService messagesService, AuthUserCache authUserCache) {
    this.proxyServer = proxyServer;
    this.networkServerCache = networkServerCache;
    this.messagesService = messagesService;
    this.authUserCache = authUserCache;
  }


  @Execute
  void execute(@Context Player player) {

    NetworkServer networkServer = this.networkServerCache.getLeastCrowded("lobby");
    if (networkServer.isOffline()) {
      this.messagesService.message("cannot.find.online.lobby").send(player);
      return;
    }

    if (this.networkServerCache.getCurrent().getName().equals(networkServer.getName())) {
      this.messagesService.message("already.connected.to.this.server").send(player);
      return;
    }

    player.createConnectionRequest(this.proxyServer.getServer(networkServer.getName()).get())
        .fireAndForget();
  }

}

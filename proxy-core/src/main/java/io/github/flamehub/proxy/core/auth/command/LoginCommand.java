package io.github.flamehub.proxy.core.auth.command;

import static io.github.flamehub.commons.util.CompletableFutures.NIL;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import com.velocitypowered.api.proxy.Player;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.proxy.core.ProxyMessages;
import io.github.flamehub.proxy.core.auth.AuthLobbyConnector;
import io.github.flamehub.proxy.core.auth.user.AuthUserCache;
import io.github.flamehub.proxy.core.util.BCrypt;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

@Command(name = "login", aliases = "l")
public final class LoginCommand {

  private final ProxyMessages proxyMessages;
  private final AuthUserCache authUserCache;
  private final AuthLobbyConnector authLobbyConnector;

  public LoginCommand(
      final ProxyMessages proxyMessages,
      final AuthUserCache authUserCache,
      final AuthLobbyConnector authLobbyConnector) {
    this.proxyMessages = proxyMessages;
    this.authUserCache = authUserCache;
    this.authLobbyConnector = authLobbyConnector;
  }

  @Execute
  public CompletableFuture<Void> execute(
      final @Context Player player,
      final @Arg String password) {

    return supplyAsync(() -> authUserCache.findByName(player.getUsername()))
        .thenCompose(context -> {
          if (context.isPremium()) {
            return proxyMessages
                .playerHasPremiumAuthorization
                .deliverAsync(player);
          }

          if (!context.isRegistered()) {
            return proxyMessages
                .firstYouHaveToRegister
                .deliverAsync(player);
          }

          if (context.isLogged()) {
            return proxyMessages
                .alreadyLogged
                .deliverAsync(player);
          }

          if (!BCrypt.checkpw(password, context.getPassword())) {
            return proxyMessages
                .wrongPassword
                .deliverAsync(player);
          }

          final InetSocketAddress remoteAddress = player.getRemoteAddress();
          final InetAddress address = remoteAddress.getAddress();
          final String hostAddress = address.getHostAddress();
          authUserCache.update(context.getUniqueId(), mutator -> {
            mutator.setLogged(true);
            mutator.setAutoLogin(true);
            if (!mutator.getIpHistory().containsKey(hostAddress)) {
              mutator.getIpHistory().put(hostAddress, new Date());
            }
            proxyMessages
                .successfullyLoggedIn
                .deliver(player);
            authLobbyConnector.findLobbyAndConnect(player);
          });

          return NIL;


        });

  }
}

package io.github.flamehub.proxy.core.auth.command;

import static io.github.flamehub.commons.util.CompletableFutures.NIL;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import com.velocitypowered.api.proxy.Player;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.commons.util.RandomStringGenerator;
import io.github.flamehub.proxy.core.ProxyMessages;
import io.github.flamehub.proxy.core.auth.AuthLobbyConnector;
import io.github.flamehub.proxy.core.auth.user.AuthUserCache;
import io.github.flamehub.proxy.core.auth.user.AuthUserRepository;
import io.github.flamehub.proxy.core.message.VelocityMessage;
import io.github.flamehub.proxy.core.util.BCrypt;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@Command(name = "register", aliases = {"reg", "zarejestruj"})
public final class RegisterCommand {

  private final ProxyMessages proxyMessages;
  private final AuthUserCache authUserCache;
  private final AuthLobbyConnector authLobbyConnector;

  public RegisterCommand(
      final ProxyMessages proxyMessages,
      final AuthUserCache authUserCache,
      final AuthLobbyConnector authLobbyConnector) {
    this.proxyMessages = proxyMessages;
    this.authUserCache = authUserCache;
    this.authLobbyConnector = authLobbyConnector;
  }

  @Execute
  CompletableFuture<Void> execute(
      final @Context Player player,
      final @Arg String captcha,
      final @Arg String password,
      final @Arg String confirmPassword) {

    return supplyAsync(() -> authUserCache.findByName(player.getUsername()))
        .thenCompose(context -> {

          if (context.isRegistered()) {
            return proxyMessages
                .alreadyRegistered
                .deliverAsync(player);
          }

          if (context.isPremium()) {
            return proxyMessages
                .playerHasPremiumAuthorization
                .deliverAsync(player);
          }

          if (!captcha.equals(context.getCaptcha())) {
            context.setCaptcha(RandomStringGenerator.generateStringWFromRandomCharacters(
                ThreadLocalRandom.current().nextInt(4, 7)));
            return proxyMessages
                .wrongCaptcha
                .deliverAsync(player);
          }

          if (!password.equals(confirmPassword)) {
            return proxyMessages
                .passwordAreNotTheSame
                .deliverAsync(player);
          }

          if (password.length() < 6 || password.length() > 32) {
            return proxyMessages
                .wrongPasswordLength
                .deliverAsync(player);
          }

          return authUserCache.mutate(context.getUniqueId(), mutator -> {
                mutator.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
                mutator.setLogged(true);
              })
              .thenRun(() -> {
                proxyMessages
                    .successfullyRegistered
                    .deliverAsync(player);
                authLobbyConnector.findLobbyAndConnect(player);
              });

        });

  }
}

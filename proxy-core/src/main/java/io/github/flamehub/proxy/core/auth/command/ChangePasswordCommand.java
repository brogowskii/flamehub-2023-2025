package io.github.flamehub.proxy.core.auth.command;

import com.velocitypowered.api.proxy.Player;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.proxy.core.ProxyMessages;
import io.github.flamehub.proxy.core.auth.user.AuthUserCache;
import io.github.flamehub.proxy.core.util.BCrypt;
import java.util.concurrent.CompletableFuture;

@Command(name = "changepassword", aliases = {"changepass", "zmienhaslo"})
public final class ChangePasswordCommand {

  private final AuthUserCache authUserCache;
  private final ProxyMessages proxyMessages;

  public ChangePasswordCommand(final AuthUserCache authUserCache,
      final ProxyMessages proxyMessages) {
    this.authUserCache = authUserCache;
    this.proxyMessages = proxyMessages;
  }

  @Execute
  CompletableFuture<Void> execute(
      final @Context Player player,
      final @Arg String oldPassword,
      final @Arg String newPassword) {

    return CompletableFuture.supplyAsync(() -> authUserCache.findByName(player.getUsername()))
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

          if (!context.isLogged()) {
            return proxyMessages
                .firstYouHaveToLogin
                .deliverAsync(player);
          }

          if (!BCrypt.checkpw(oldPassword, context.getPassword())) {
            return proxyMessages
                .wrongPassword
                .deliverAsync(player);
          }

          if (newPassword.length() < 6 || newPassword.length() > 32) {
            return proxyMessages
                .wrongPasswordLength
                .deliverAsync(player);
          }

          return authUserCache.mutate(context.getUniqueId(), mutator -> {
            mutator.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
          }).thenRun(
              () -> proxyMessages
                  .successfullyChangedPassword
                  .deliver(player));

        });

  }

}

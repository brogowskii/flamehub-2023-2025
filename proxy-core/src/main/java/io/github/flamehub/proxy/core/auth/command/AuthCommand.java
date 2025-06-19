package io.github.flamehub.proxy.core.auth.command;

import static io.github.flamehub.commons.util.CompletableFutures.NIL;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.util.TimeUtil;
import io.github.flamehub.proxy.core.ProxyMessages;
import io.github.flamehub.proxy.core.auth.user.AuthUser;
import io.github.flamehub.proxy.core.auth.user.AuthUserCache;
import io.github.flamehub.proxy.core.auth.user.AuthUserRepository;
import io.github.flamehub.proxy.core.message.VelocityMessage;
import io.github.flamehub.proxy.core.player.PlayerKickPacket;
import io.github.flamehub.proxy.core.util.BCrypt;
import io.github.flamehub.proxy.core.util.TextUtil;
import java.util.concurrent.CompletableFuture;

@Permission("server.commands.auth")
@Command(name = "auth", aliases = "authorization")
public final class AuthCommand {

  private final ProxyServer proxyServer;
  private final ProxyMessages proxyMessages;
  private final AuthUserRepository authUserRepository;
  private final AuthUserCache authUserCache;
  private final RedisMessenger redisMessenger;

  public AuthCommand(
      final ProxyServer proxyServer, final ProxyMessages proxyMessages,
      final AuthUserRepository authUserRepository,
      final AuthUserCache authUserCache,
      final RedisMessenger redisMessenger
  ) {
    this.proxyServer = proxyServer;
    this.proxyMessages = proxyMessages;
    this.authUserRepository = authUserRepository;
    this.authUserCache = authUserCache;
    this.redisMessenger = redisMessenger;
  }

  @Execute(name = "removeuser")
  @Permission("server.commands.auth.removeuser")
  CompletableFuture<Void> removeUser(
      final @Context CommandSource commandSource,
      final @Arg("networkPlayer") String name) {

    return supplyAsync(() -> authUserCache.findByName(name))
        .thenAccept(context -> {

          if (context == null) {
            proxyMessages
                .userDoesNotExists
                .deliverAsync(commandSource);
            return;
          }

          authUserCache.remove(context);
          authUserRepository.delete(context);

          Player player = proxyServer.getPlayer(context.getUniqueId()).orElse(null);
          if (player != null) {
            player.disconnect(TextUtil.parse("&cUzytkownik został usunięty z bazy danych!"));
          }

        });

  }


  @Execute(name = "accounts")
  @Permission("server.commands.auth.accounts")
  CompletableFuture<Void> accounts(
      final @Context CommandSource commandSource,
      final @Arg("playerName") String playerName) {

    return CompletableFuture.supplyAsync(() -> authUserCache.findByName(playerName))
        .thenAccept(authUser -> {

          if (authUser == null) {
            proxyMessages
                .userDoesNotExists
                .deliver(commandSource);
            return;
          }

          commandSource.sendMessage(TextUtil.parse("&7Lista użytkowników z tym samym adresem IP:"));
          for (final AuthUser it : authUserCache.findAccountsByIP(authUser.getFirstIP())) {
            commandSource.sendMessage(TextUtil.parse("&8- &f" + it.getName()));
          }
        });

  }

  @Execute(name = "iphistory")
  @Permission("server.commands.auth.iphistory")
  CompletableFuture<Void> ipHistory(
      final @Context CommandSource commandSource,
      final @Arg("networkPlayer") String name) {
    return CompletableFuture.supplyAsync(() -> authUserCache.findByName(name))
        .thenAccept(authUser -> {

          if (authUser == null) {
            proxyMessages
                .userDoesNotExists
                .deliver(commandSource);
            return;
          }

          VelocityMessage.from("&7Historia IP tego użytkownika:").deliver(commandSource);
          authUser.getIpHistory().forEach((ip, date) -> {
            VelocityMessage.from(
                    "&8- &7IP: &f" + ip + " &8| &7Data: &f" + TimeUtil.formatDate(date))
                .deliver(commandSource);

          });

        });

  }

  @Execute(name = "unregister")
  @Permission("server.commands.auth.unregister")
  CompletableFuture<Void> unregister(
      final @Context CommandSource commandSource,
      final @Arg("networkPlayer") String name) {

    return supplyAsync(() -> authUserCache.findByName(name))
        .thenCompose(context -> {

          if (context == null) {
            return proxyMessages
                .userDoesNotExists
                .deliverAsync(commandSource);
          }

          if (context.isPremium()) {
            return VelocityMessage.from(
                    "&#DA0000☹ &8〢 &#F33434Ten gracz jest zarejestrowany jako premium!")
                .deliverAsync(commandSource);
          }

          if (!context.isRegistered()) {
            return VelocityMessage.from(
                    "&#DA0000☹ &8〢 &#F33434Ten gracz nie jest zarejestrowany!")
                .deliverAsync(commandSource);
          }

          authUserCache.update(context.getUniqueId(), mutator -> {
            mutator.setPassword(null);
            mutator.setLastIP(null);

            redisMessenger.publish(
                "velocity_servers",
                new PlayerKickPacket(mutator.getName(), "&aZostałeś pomyślnie odrejestrowany!")
            );

            VelocityMessage.from(
                    "&7Pomyślnie odrejestrowano gracza: &a" + mutator.getName() + "&7!")
                .deliver(commandSource);

          });

          return NIL;


        });

  }


  @Execute(name = "changepassword")
  @Permission("server.commands.auth.changepassword")
  CompletableFuture<Void> changePassword(
      final @Context CommandSource commandSource,
      final @Arg("networkPlayer") String name,
      final @Arg String password) {

    return supplyAsync(() -> authUserCache.findByName(name))
        .thenCompose(context -> {

          if (context == null) {
            return proxyMessages
                .userDoesNotExists
                .deliverAsync(commandSource);
          }

          if (context.isPremium()) {
            return VelocityMessage.from(
                    "&#DA0000☹ &8〢 &#F33434Ten gracz jest zarejestrowany jako premium!")
                .deliverAsync(commandSource);
          }

          if (!context.isRegistered()) {
            return VelocityMessage.from(
                    "&#DA0000☹ &8〢 &#F33434Ten gracz nie jest zarejestrowany!")
                .deliverAsync(commandSource);
          }

          if (password.length() < 6 || password.length() > 32) {
            return proxyMessages
                .wrongPasswordLength
                .deliverAsync(commandSource);
          }

          final String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
          authUserCache.update(context.getUniqueId(),
              mutator -> {
                mutator.setPassword(hashedPassword);
                VelocityMessage.from(
                        "&7Pomyślnie ustawiono nowe hasło dla gracza: &a" + mutator.getName() + "&7!")
                    .deliver(commandSource);
              });

          return NIL;

        });

  }

  @Execute(name = "allowvpn")
  @Permission("server.commands.auth.allowvpn")
  CompletableFuture<Void> allowVpn(
      final @Context CommandSource commandSource,
      final @Arg("networkPlayer") String name) {

    return supplyAsync(() -> authUserCache.findByName(name))
        .thenCompose(context -> {

          if (context == null) {
            return proxyMessages
                .userDoesNotExists
                .deliverAsync(commandSource);
          }

          authUserCache.update(context.getUniqueId(), mutator -> {
            mutator.setVpnAllowed(!mutator.isVpnAllowed());
          });

          return VelocityMessage.from(
                  "&7Możliwość dołączania poprzez vpn dla tego gracza została: " + (
                      context.isVpnAllowed()
                          ? "&azezwolona." : "&czakazana."))
              .deliverAsync(commandSource);

        });

  }

  @Execute(name = "searchUsersByIP")
  @Permission("server.commands.auth.searchusersbyip")
  CompletableFuture<Void> searchAccountsByIP(
      final @Context CommandSource commandSource,
      final @Arg String ipAddress) {

    return supplyAsync(() -> authUserRepository.loadAll("lastIP", ipAddress))
        .thenAccept(authUsers -> {

          if (authUsers == null || authUsers.isEmpty()) {
            VelocityMessage.from(
                    "&#DA0000☹ &8〢 &#F33434Nie znaleziono żadnego użytkownika o podanym adresie IP!")
                .deliver(commandSource);
            return;
          }

          VelocityMessage.from("&7Lista użytkowników o tym adresie IP:").deliver(commandSource);
          for (AuthUser authUser : authUsers) {
            VelocityMessage.from("&8- &f" + authUser.getName()).deliver(commandSource);
          }

        });


  }

  @Execute(name = "info")
  @Permission("server.commands.auth.info")
  CompletableFuture<Void> info(
      final @Context CommandSource commandSource,
      final @Arg("networkPlayer") String name) {

    return supplyAsync(() -> authUserCache.findByName(name))
        .thenAccept(authUser -> {

          if (authUser == null) {
            proxyMessages
                .userDoesNotExists
                .deliver(commandSource);
            return;
          }

          VelocityMessage.from(
              "",
              " &7UUID&8: &f" + authUser.getUniqueId().toString() + " &8(&7v"
                  + authUser.getUniqueId()
                  .version() + "&8)",
              " &7Username&8: &f" + authUser.getName(),
              " &7First IP&8: &f" + authUser.getFirstIP(),
              " &7Last IP&8: &f" + authUser.getFirstIP(),
              " &7First login&8: &f" + TimeUtil.formatDate(authUser.getFirstLoginDate()),
              " &7Last login&8: &f" + TimeUtil.formatDate(authUser.getLastLoginDate()),
              " &7Premium account&8: &f" + authUser.isPremium(),
              " &7Registered&8: &f" + (authUser.isPremium() ? "premium" : authUser.isRegistered()),
              " &7Logged in&8: &f" + authUser.isLogged(),
              " &7Autologin&8: &f" + (authUser.isPremium() ? "premium" : authUser.isAutoLogin()),
              " &7VPN Allowed&8: &f" + authUser.isVpnAllowed(),
              ""
          ).deliver(commandSource);


        });

  }

}

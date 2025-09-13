package io.github.flamehub.proxy.core.auth;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import io.github.flamehub.proxy.core.auth.user.AuthUser;
import io.github.flamehub.proxy.core.auth.user.AuthUserCache;
import io.github.flamehub.proxy.core.util.TextUtil;
import java.time.Duration;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

public final class AuthTask implements Runnable {

  private final ProxyServer proxyServer;
  private final AuthUserCache authUserCache;

  public AuthTask(final ProxyServer proxyServer, final AuthUserCache authUserCache) {
    this.proxyServer = proxyServer;
    this.authUserCache = authUserCache;
  }

  @Override
  public void run() {

    for (final Player player : proxyServer.getAllPlayers()) {
      final Optional<ServerConnection> optionalCurrentServer = player.getCurrentServer();
      if (optionalCurrentServer.isEmpty()) {
        continue;
      }

      if ("auth".equals(optionalCurrentServer.get().getServerInfo().getName())) {
        final AuthUser authUser = authUserCache.findByName(player.getUsername());
        Title title = Title.title(Component.empty(), Component.empty());
        final Title.Times times = Title.Times.times(
            Duration.ofSeconds(0),
            Duration.ofSeconds(2),
            Duration.ofSeconds(1)
        );

        if (!authUser.isPremium()) {
          if (!authUser.isRegistered()) {
            title = Title.title(
                TextUtil.parse("&6&lREJESTRACJA"),
                TextUtil.parse(
                    "&7Zarejestruj się: &e/register " + authUser.getCaptcha() + " <hasło> <hasło>"),
                times
            );

          } else if (!authUser.isLogged()) {

            title = Title.title(
                TextUtil.parse("&6&lLOGOWANIE"),
                TextUtil.parse("&7Zaloguj się: &e/login <hasło>"),
                times
            );

          }
        } else {

          title = Title.title(
              Component.empty(),
              TextUtil.parse("&aSzukam najmniej obciążonego lobby..."),
              times
          );

        }

        player.showTitle(title);
      }
    }
  }

}

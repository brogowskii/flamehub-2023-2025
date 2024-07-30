package io.github.flamehub.proxy.core.auth.command;

import com.velocitypowered.api.proxy.Player;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.proxy.core.auth.AuthLobbyConnector;
import io.github.flamehub.proxy.core.auth.user.AuthUser;
import io.github.flamehub.proxy.core.auth.user.AuthUserCache;
import io.github.flamehub.proxy.core.auth.user.AuthUserRepository;
import io.github.flamehub.proxy.core.message.VelocityMessage;
import io.github.flamehub.proxy.core.util.BCrypt;
import java.util.Date;

@Command(name = "login", aliases = "l")
public class LoginCommand {

  private final AuthUserCache authUserCache;
  private final AuthUserRepository authUserRepository;
  private final AuthLobbyConnector authLobbyConnector;

  public LoginCommand(AuthUserCache authUserCache, AuthUserRepository authUserRepository,
      AuthLobbyConnector authLobbyConnector) {
    this.authUserCache = authUserCache;
    this.authUserRepository = authUserRepository;
    this.authLobbyConnector = authLobbyConnector;
  }

  @Execute
  public void execute(@Context Player player, @Arg String password) {
    AuthUser authUser = this.authUserCache.findByName(player.getUsername());
    if (authUser.isPremium()) {
      VelocityMessage.from("&cJesteś graczem premium!").send(player);
      return;
    }

    if (!authUser.isRegistered()) {
      VelocityMessage.from("&cNajpierw musisz sie zarejestrować!").send(player);
      return;
    }

    if (authUser.isLogged()) {
      VelocityMessage.from("&cJesteś już zalogowany!").send(player);
      return;
    }

    if (!BCrypt.checkpw(password, authUser.getPassword())) {
      VelocityMessage.from("&cPodane hasło jest nieprawidłowe!").send(player);
      return;
    }

    VelocityMessage.from("&aZostałeś pomyślnie zalogowany!").send(player);
    authUser.setLogged(true);
    authUser.setAutoLogin(true);

    String hostAddress = player.getRemoteAddress().getAddress().getHostAddress();
    if (!authUser.getIpHistory().containsKey(hostAddress)) {
      authUser.getIpHistory().put(hostAddress, new Date());
    }

    this.authUserRepository.save(authUser);
    this.authLobbyConnector.findLobbyAndConnect(player);
  }
}

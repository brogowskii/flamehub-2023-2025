package io.github.flamehub.proxy.core.auth.command;

import com.velocitypowered.api.proxy.Player;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.proxy.core.auth.AuthLobbyConnector;
import io.github.flamehub.proxy.core.auth.user.AuthUser;
import io.github.flamehub.proxy.core.auth.user.AuthUserCache;
import io.github.flamehub.proxy.core.auth.util.BCrypt;
import io.github.flamehub.proxy.core.text.TextBuilder;

@Command(name = "login", aliases = "l")
public class LoginCommand {

    private final AuthUserCache authUserCache;
    private final AuthLobbyConnector authLobbyConnector;

    public LoginCommand(AuthUserCache authUserCache, AuthLobbyConnector authLobbyConnector) {
        this.authUserCache = authUserCache;
        this.authLobbyConnector = authLobbyConnector;
    }

    @Execute
    public void execute(@Context Player player, @Arg String password) {
        AuthUser authUser = this.authUserCache.findByName(player.getUsername());
        if (authUser.isPremium()) {
            TextBuilder.builder()
                    .text("&cJesteś graczem premium!")
                    .send(player);
            return;
        }

        if (!authUser.isRegistered()) {
            TextBuilder.builder()
                    .text("&cNajpierw musisz sie zarejestrować!")
                    .send(player);
            return;
        }

        if (authUser.isLogged()) {
            TextBuilder.builder()
                    .text("&cJesteś już zalogowany!")
                    .send(player);
            return;
        }

        if (!BCrypt.checkpw(password, authUser.getPassword())) {
            TextBuilder.builder()
                    .text("&cPodane hasło jest nieprawidłowe!")
                    .send(player);
            return;
        }

        TextBuilder.builder()
                .text("&aZostałeś pomyślnie zalogowany!")
                .send(player);

        authUser.setLogged(true);
        this.authLobbyConnector.findLobbyAndConnect(player);
    }
}

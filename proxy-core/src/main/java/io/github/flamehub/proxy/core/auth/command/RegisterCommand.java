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
import io.github.flamehub.proxy.core.util.BCrypt;
import io.github.flamehub.proxy.core.message.VelocityMessage;
import io.github.flamehub.commons.util.RandomStringGenerator;

import java.util.concurrent.ThreadLocalRandom;

@Command(name = "register", aliases = {"reg", "zarejestruj"})
public class RegisterCommand {

    private final AuthUserCache authUserCache;
    private final AuthUserRepository authUserRepository;
    private final AuthLobbyConnector authLobbyConnector;

    public RegisterCommand(AuthUserCache authUserCache, AuthUserRepository authUserRepository, AuthLobbyConnector authLobbyConnector) {
        this.authUserCache = authUserCache;
        this.authUserRepository = authUserRepository;
        this.authLobbyConnector = authLobbyConnector;
    }

    @Execute
    public void execute(@Context Player player, @Arg String captcha, @Arg String password, @Arg String confirmPassword) {
        AuthUser authUser = this.authUserCache.findByName(player.getUsername());
        if (authUser.isRegistered()) {
            VelocityMessage.from("&cJesteś juz zarejestrowany!").send(player);
            return;
        }

        if (authUser.isPremium()) {
            VelocityMessage.from("&cJesteś graczem premium!").send(player);
            return;
        }

        if (!captcha.equals(authUser.getCaptcha())) {
            VelocityMessage.from("&cKod captcha jest nieprawidłowy! Spróbuj ponownie.").send(player);
            authUser.setCaptcha(RandomStringGenerator.generateStringWFromRandomCharacters(ThreadLocalRandom.current().nextInt(4, 7)));
            return;
        }

        if (!password.equals(confirmPassword)) {
            VelocityMessage.from("&Hasła się nie zgadzają!").send(player);
            return;
        }

        if (password.length() < 6 || password.length() > 32) {
            VelocityMessage.from("&cHasło musi mieć &46-32 &cznaków!").send(player);
            return;
        }

        VelocityMessage.from("&aZostałeś pomyślnie zarejestrowany!").send(player);
        authUser.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        authUser.setLogged(true);
        this.authLobbyConnector.findLobbyAndConnect(player);
        this.authUserRepository.save(authUser);
    }
}

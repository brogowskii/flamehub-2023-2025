package io.github.flamehub.proxy.core.auth.command;

import com.velocitypowered.api.proxy.Player;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.proxy.core.auth.user.AuthUser;
import io.github.flamehub.proxy.core.auth.user.AuthUserCache;
import io.github.flamehub.proxy.core.auth.user.AuthUserRepository;
import io.github.flamehub.proxy.core.util.BCrypt;
import io.github.flamehub.proxy.core.message.VelocityMessage;

@Command(name = "changepassword", aliases = {"changepass", "zmienhaslo"})
public final class ChangePasswordCommand {

    private final AuthUserCache authUserCache;
    private final AuthUserRepository authUserRepository;

    public ChangePasswordCommand(AuthUserCache authUserCache, AuthUserRepository authUserRepository) {
        this.authUserCache = authUserCache;
        this.authUserRepository = authUserRepository;
    }

    @Execute
    void execute(@Context Player player, @Arg String oldPassword, @Arg String newPassword) {
        AuthUser authUser = this.authUserCache.findByName(player.getUsername());
        if (authUser.isPremium()) {
            VelocityMessage.from("&cJesteś graczem premium!").send(player);
            return;
        }

        if (!authUser.isRegistered()) {
            VelocityMessage.from("&cNajpierw musisz sie zarejestrować!").send(player);
            return;
        }

        if (!authUser.isLogged()) {
            VelocityMessage.from("&cNajpierw musisz sie zalogować!").send(player);
            return;
        }

        if (!BCrypt.checkpw(oldPassword, authUser.getPassword())) {
            VelocityMessage.from("&cStare hasło jest nieprawidłowe!").send(player);
            return;
        }

        if (newPassword.length() < 6 || newPassword.length() > 32) {
            VelocityMessage.from("&cHasło musi mieć &46-32 &cznaków!").send(player);
            return;
        }

        authUser.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        this.authUserRepository.save(authUser);

    }

}

package io.github.flamehub.proxy.core.auth.command;

import com.velocitypowered.api.proxy.Player;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.proxy.core.auth.user.AuthUser;
import io.github.flamehub.proxy.core.auth.user.AuthUserCache;
import io.github.flamehub.proxy.core.auth.user.AuthUserRepository;
import io.github.flamehub.proxy.core.auth.util.BCrypt;
import io.github.flamehub.proxy.core.text.TextBuilder;

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

        if (!authUser.isLogged()) {
            TextBuilder.builder()
                    .text("&cNajpierw musisz sie zalogować!")
                    .send(player);
            return;
        }

        if (!BCrypt.checkpw(oldPassword, authUser.getPassword())) {
            TextBuilder.builder()
                    .text("&cStare hasło jest nieprawidłowe!")
                    .send(player);
            return;
        }

        if (newPassword.length() < 6 || newPassword.length() > 32) {
            TextBuilder.builder()
                    .text("&cHasło musi mieć &46-32 &cznaków!")
                    .send(player);
            return;
        }

        authUser.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        this.authUserRepository.save(authUser);

    }

}

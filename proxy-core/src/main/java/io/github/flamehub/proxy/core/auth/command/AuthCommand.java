package io.github.flamehub.proxy.core.auth.command;

import com.velocitypowered.api.command.CommandSource;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.proxy.core.auth.user.AuthUser;
import io.github.flamehub.proxy.core.auth.user.AuthUserCache;
import io.github.flamehub.proxy.core.auth.user.update.AuthUserUpdateBuilder;
import io.github.flamehub.proxy.core.auth.user.update.AuthUserUpdater;
import io.github.flamehub.proxy.core.auth.util.BCrypt;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.proxy.core.locale.VelocityMessagesService;
import io.github.flamehub.proxy.core.player.PlayerKickPacket;
import io.github.flamehub.proxy.core.text.TextBuilder;
import io.github.flamehub.proxy.core.text.TextUtil;
import io.github.flamehub.commons.util.TimeUtil;

@Permission("server.commands.auth")
@Command(name = "auth", aliases = "authorization")
public final class AuthCommand {

    private final AuthUserUpdater authUserUpdater;
    private final AuthUserCache authUserCache;
    private final VelocityMessagesService messagesService;
    private final RedisMessenger redisMessenger;

    public AuthCommand(
            AuthUserUpdater authUserUpdater,
            AuthUserCache authUserCache,
            VelocityMessagesService messagesService,
            RedisMessenger redisMessenger
    ) {
        this.authUserUpdater = authUserUpdater;
        this.authUserCache = authUserCache;
        this.messagesService = messagesService;
        this.redisMessenger = redisMessenger;
    }


    @Execute(name = "accounts")
    public void accounts(@Context CommandSource commandSource, @Arg String playerName) {
        AuthUser authUser = this.authUserCache.findByName(playerName);
        if (authUser == null) {
            this.messagesService.getAsText("user.does.not.exist")
                    .placeholder("{NAME}", playerName)
                    .send(commandSource);
            return;
        }

        for (AuthUser it : this.authUserCache.findAccountsByIP(authUser.getIpAddress())) {
            commandSource.sendMessage(TextUtil.parse("&8- &7" + it.getName()));
        }
    }

    @Execute(name = "unregister")
    public void unregister(@Context CommandSource commandSource, @Arg String playerName) {
        AuthUser authUser = this.authUserCache.findByName(playerName);
        if (authUser == null) {
            this.messagesService.getAsText("user.does.not.exist")
                    .placeholder("{NAME}", playerName)
                    .send(commandSource);
            return;
        }

        if (authUser.isPremium()) {
            TextBuilder.builder()
                    .text("&cTen gracz jest zarejestrowany jako premium!")
                    .send(commandSource);
            return;
        }

        if (!authUser.isRegistered()) {
            TextBuilder.builder()
                    .text("&cTen gracz nie jest zarejestrowany!")
                    .send(commandSource);
            return;
        }


        authUser.setPassword(null);
        authUser.setRegistered(false);
        this.authUserUpdater.update(authUser, AuthUserUpdateBuilder.builder()
                .id(authUser.getName())
                .fieldValue("password", authUser.getPassword())
                .fieldValue("registered", authUser.isRegistered())
                .build());

        this.redisMessenger.publish(
                "velocity_servers",
                new PlayerKickPacket(authUser.getName(), "&aZostałeś pomyślnie odrejestrowany!")
        );

        TextBuilder.builder()
                .text("&7Pomyślnie odrejestrowano gracza: &a" + authUser.getName() + "&7!")
                .send(commandSource);
    }


    @Execute(name = "changepassword")
    public void changePassword(@Context CommandSource commandSource, @Arg String playerName, @Arg String password) {
        AuthUser authUser = this.authUserCache.findByName(playerName);
        if (authUser == null) {
            this.messagesService.getAsText("user.does.not.exist")
                    .placeholder("{NAME}", playerName)
                    .send(commandSource);
            return;
        }

        if (authUser.isPremium()) {
            TextBuilder.builder()
                    .text("&cTen gracz jest zarejestrowany jako premium!")
                    .send(commandSource);
            return;
        }

        if (!authUser.isRegistered()) {
            TextBuilder.builder()
                    .text("&cTen gracz nie jest zarejestrowany!")
                    .send(commandSource);
            return;
        }

        if (password.length() < 6 || password.length() > 32) {
            TextBuilder.builder()
                    .text("&cHasło musi mieć 6-32 znaków!")
                    .send(commandSource);
            return;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        authUser.setPassword(hashedPassword);
        this.authUserUpdater.update(authUser, AuthUserUpdateBuilder.builder()
                .id(authUser.getName())
                .fieldValue("password", authUser.getPassword())
                .build());

        TextBuilder.builder()
                .text("&7Pomyślnie ustawiono nowe hasło dla gracza: &a" + authUser.getName() + "&7!")
                .send(commandSource);
    }

    @Execute(name = "info")
    public void info(@Context CommandSource commandSource, @Arg String playerName) {
        AuthUser authUser = this.authUserCache.findByName(playerName);
        if (authUser == null) {
            this.messagesService.getAsText("user.does.not.exist")
                    .placeholder("{NAME}", playerName)
                    .send(commandSource);
            return;
        }

        commandSource.sendMessage(TextUtil.parse(""));
        commandSource.sendMessage(TextUtil.parse("&8» &7Nick&8: &f" + authUser.getName()));
        commandSource.sendMessage(TextUtil.parse("&8» &7IP&8: &f" + authUser.getIpAddress()));
        commandSource.sendMessage(TextUtil.parse("&8» &7Data rejestracji&8: &f" + TimeUtil.formatDate(authUser.getFirstJoinTime())));
        commandSource.sendMessage(TextUtil.parse("&8» &7Premium&8: &f" + authUser.isPremium()));
        commandSource.sendMessage(TextUtil.parse("&8» &7Zarejestrowany&8: &f" + authUser.isRegistered()));
        commandSource.sendMessage(TextUtil.parse("&8» &7Zalogowany&8: &f" + authUser.isLogged()));
        commandSource.sendMessage(TextUtil.parse(""));
    }

}

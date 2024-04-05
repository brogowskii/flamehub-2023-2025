package io.github.flamehub.proxy.core.auth.command;

import com.velocitypowered.api.command.CommandSource;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.proxy.core.auth.user.AuthUser;
import io.github.flamehub.proxy.core.auth.user.AuthUserCache;
import io.github.flamehub.proxy.core.auth.user.AuthUserRepository;
import io.github.flamehub.proxy.core.auth.user.update.AuthUserUpdateBuilder;
import io.github.flamehub.proxy.core.auth.user.update.AuthUserUpdater;
import io.github.flamehub.proxy.core.util.BCrypt;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.proxy.core.message.VelocityMessage;
import io.github.flamehub.proxy.core.message.VelocityMessagesService;
import io.github.flamehub.proxy.core.player.PlayerKickPacket;
import io.github.flamehub.proxy.core.util.TextUtil;
import io.github.flamehub.commons.util.TimeUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Permission("server.commands.auth")
@Command(name = "auth", aliases = "authorization")
public final class AuthCommand {

    private final AuthUserRepository authUserRepository;
    private final AuthUserUpdater authUserUpdater;
    private final AuthUserCache authUserCache;
    private final VelocityMessagesService messagesService;
    private final RedisMessenger redisMessenger;

    public AuthCommand(
            AuthUserRepository authUserRepository, AuthUserUpdater authUserUpdater,
            AuthUserCache authUserCache,
            VelocityMessagesService messagesService,
            RedisMessenger redisMessenger
    ) {
        this.authUserRepository = authUserRepository;
        this.authUserUpdater = authUserUpdater;
        this.authUserCache = authUserCache;
        this.messagesService = messagesService;
        this.redisMessenger = redisMessenger;
    }


    @Execute(name = "accounts")
    void accounts(@Context CommandSource commandSource, @Arg String playerName) {
        AuthUser authUser = this.authUserCache.findByName(playerName);
        if (authUser == null) {
            this.messagesService.message("user.does.not.exist")
                    .with("name", playerName)
                    .send(commandSource);
            return;
        }

        commandSource.sendMessage(TextUtil.parse("&7Lista użytkowników z tym samym adresem IP:"));
        for (AuthUser it : this.authUserCache.findAccountsByIP(authUser.getFirstIP())) {
            commandSource.sendMessage(TextUtil.parse("&8- &f" + it.getName()));
        }
    }

    @Execute(name = "iphistory")
    void ipHistory(@Context CommandSource commandSource, @Arg String playerName) {
        AuthUser authUser = this.authUserCache.findByName(playerName);
        if (authUser == null) {
            this.messagesService.message("user.does.not.exist")
                    .with("name", playerName)
                    .send(commandSource);
            return;
        }

        VelocityMessage.from("&7Historia IP tego użytkownika:").send(commandSource);
        for (Map.Entry<String, Date> stringDateEntry : authUser.getIpHistory().entrySet()) {

            Date value = stringDateEntry.getValue();
            String key = stringDateEntry.getKey();

            VelocityMessage.from("&8- &7IP: &f" + key + " &8| &7Data: &f" + TimeUtil.formatDate(value)).send(commandSource);
        }

    }

    @Execute(name = "unregister")
    void unregister(@Context CommandSource commandSource, @Arg String playerName) {
        AuthUser authUser = this.authUserCache.findByName(playerName);
        if (authUser == null) {
            this.messagesService.message("user.does.not.exist")
                    .with("name", playerName)
                    .send(commandSource);
            return;
        }

        if (authUser.isPremium()) {
            VelocityMessage.from("&cTen gracz jest zarejestrowany jako premium!").send(commandSource);
            return;
        }

        if (!authUser.isRegistered()) {
            VelocityMessage.from("&cTen gracz nie jest zarejestrowany!").send(commandSource);
            return;
        }

        authUser.setPassword(null);
        authUser.setLastIP(null);
        this.authUserUpdater.update(authUser, AuthUserUpdateBuilder.builder()
                .id(authUser.getName())
                .fieldValue("password", authUser.getPassword())
                .fieldValue("registered", authUser.isRegistered())
                .build());

        this.redisMessenger.publish(
                "velocity_servers",
                new PlayerKickPacket(authUser.getName(), "&aZostałeś pomyślnie odrejestrowany!")
        );

        VelocityMessage.from("&7Pomyślnie odrejestrowano gracza: &a" + authUser.getName() + "&7!").send(commandSource);
    }


    @Execute(name = "changepassword")
    void changePassword(@Context CommandSource commandSource, @Arg String playerName, @Arg String password) {
        AuthUser authUser = this.authUserCache.findByName(playerName);
        if (authUser == null) {
            this.messagesService.message("user.does.not.exist")
                    .with("name", playerName)
                    .send(commandSource);
            return;
        }

        if (authUser.isPremium()) {
            VelocityMessage.from("&cTen gracz jest zarejestrowany jako premium!").send(commandSource);
            return;
        }

        if (!authUser.isRegistered()) {
            VelocityMessage.from("&cTen gracz nie jest zarejestrowany!").send(commandSource);
            return;
        }

        if (password.length() < 6 || password.length() > 32) {
            VelocityMessage.from("&cHasło musi mieć 6-32 znaków!").send(commandSource);
            return;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        authUser.setPassword(hashedPassword);
        this.authUserUpdater.update(authUser, AuthUserUpdateBuilder.builder()
                .id(authUser.getName())
                .fieldValue("password", authUser.getPassword())
                .build());

        VelocityMessage.from("&7Pomyślnie ustawiono nowe hasło dla gracza: &a" + authUser.getName() + "&7!").send(commandSource);
    }

    @Execute(name = "allowvpn")
    void allowVpn(@Context CommandSource commandSource, @Arg String playerName) {
        AuthUser authUser = this.authUserCache.findByName(playerName);
        if (authUser == null) {
            this.messagesService.message("user.does.not.exist")
                    .with("name", playerName)
                    .send(commandSource);
            return;
        }

        authUser.setVpnAllowed(!authUser.isVpnAllowed());
        this.authUserRepository.save(authUser);
        VelocityMessage.from("&7Możliwość dołączania poprzez vpn dla tego gracza została: " + (authUser.isVpnAllowed() ? "&azezwolona." : "&czakazana."))
                .send(commandSource);
    }

    @Execute(name = "searchUsersByIP")
    void searchAccountsByIP(@Context CommandSource commandSource, @Arg String ipAddress) {
        List<AuthUser> usersByIP = this.authUserRepository.loadAll("lastIP", ipAddress);
        if (usersByIP == null || usersByIP.isEmpty()) {
            VelocityMessage.from("&cNie znaleziono żadnego użytkownika o podanym adresie IP!").send(commandSource);
            return;
        }

        VelocityMessage.from("&7Lista użytkowników o tym adresie IP:").send(commandSource);
        for (AuthUser authUser : usersByIP) {
            VelocityMessage.from("&8- &f" + authUser.getName()).send(commandSource);
        }

    }

    @Execute(name = "info")
    void info(@Context CommandSource commandSource, @Arg String playerName) {
        AuthUser authUser = this.authUserCache.findByName(playerName);
        if (authUser == null) {
            this.messagesService.message("user.does.not.exist")
                    .with("name", playerName)
                    .send(commandSource);
            return;
        }

        VelocityMessage.from(
                "",
                " &7UUID&8: &f" + authUser.getUniqueId().toString() + " &8(&7v"+  authUser.getUniqueId().version() + "&8)",
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
        ).send(commandSource);

    }

}

package io.github.flamehub.proxy.core.auth;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.proxy.core.ProxyCore;
import io.github.flamehub.proxy.core.auth.user.AuthUser;
import io.github.flamehub.proxy.core.auth.user.AuthUserCache;
import io.github.flamehub.proxy.core.auth.user.AuthUserRepository;
import io.github.flamehub.proxy.core.auth.util.AuthorizationChecker;
import io.github.flamehub.proxy.core.locale.VelocityMessagesService;
import io.github.flamehub.proxy.core.text.TextUtil;
import io.github.flamehub.commons.util.TimeUtil;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public final class AuthListener {


    private final ProxyServer proxyServer;
    private final NetworkPlayerCache networkPlayerCache;
    private final AuthUserCache authUserCache;
    private final AuthUserRepository authUserRepository;
    private final VelocityMessagesService messagesService;
    private final AuthLobbyConnector authLobbyConnector;

    public AuthListener(
            ProxyServer proxyServer, NetworkPlayerCache networkPlayerCache,
            AuthUserCache authUserCache,
            AuthUserRepository authUserRepository,
            VelocityMessagesService messagesService,
            AuthLobbyConnector authLobbyConnector
    ) {
        this.proxyServer = proxyServer;
        this.networkPlayerCache = networkPlayerCache;
        this.authUserCache = authUserCache;
        this.authUserRepository = authUserRepository;
        this.messagesService = messagesService;
        this.authLobbyConnector = authLobbyConnector;
    }

    @Subscribe(order = PostOrder.LAST)
    public void onPreLogin(PreLoginEvent event) {
        if (!event.getResult().isAllowed()) {
            return;
        }

        String name = event.getUsername();
        Optional<NetworkPlayer> networkPlayer = Optional.ofNullable(this.networkPlayerCache.findByName(name));
        if (networkPlayer.isPresent()) {
            event.setResult(TextUtil.preDenied(this.messagesService.getMessage("player.already.online")));
            return;
        }

        if (name.length() < 3 || name.length() > 16) {
            event.setResult(TextUtil.preDenied(this.messagesService.getMessage("not.allowed.nickname")));
            return;
        }

        AuthUser authUser = this.authUserCache.findByName(name);
        Instant now = Instant.now();
        if (authUser == null) {
            String hostAddress = event.getConnection().getRemoteAddress().getAddress().getHostAddress();
            if (this.authUserCache.findAccountsByIP(hostAddress).size() >= 3) {
                event.setResult(TextUtil.preDenied(this.messagesService.getMessage("accounts.limit.reached")));
                return;
            }

            authUser = new AuthUser(name);
            authUser.setPremium(AuthorizationChecker.isPremiumAccount(name));
            authUser.setFirstJoinTime(now);
            authUser.setIpAddress(hostAddress);
            this.authUserRepository.save(authUser);
        }

        this.authUserCache.add(authUser);
        if (!authUser.isPremium() && !authUser.getName().equals(name)) {
            event.setResult(TextUtil.preDenied(this.messagesService.getAsText("incorrect.nickname")
                    .placeholder("{NICK}", authUser.getName())
                    .firstLine()));
            return;
        }

        if (authUser.getConnectionDelay().isAfter(now)) {
            event.setResult(TextUtil.preDenied(this.messagesService.getAsText("connection.delay")
                    .placeholder("{TIME}", TimeUtil.formatTimeSimple(Duration.between(now, authUser.getConnectionDelay())))
                    .firstLine()));
            return;
        }

        event.setResult(authUser.isPremium() ?
                PreLoginEvent.PreLoginComponentResult.forceOnlineMode() :
                PreLoginEvent.PreLoginComponentResult.forceOfflineMode());

    }

    @Subscribe
    public void onServerConnect(ServerConnectedEvent event) {
        ServerInfo serverInfo = event.getServer().getServerInfo();
        Optional<RegisteredServer> previousServer = event.getPreviousServer();
        if (serverInfo.getName().startsWith("limbo") && previousServer.isPresent()) {

            if (!previousServer.get().getServerInfo().getName().startsWith("lobby")) {
                return;
            }

            Player player = event.getPlayer();
            AuthUser authUser = this.authUserCache.findByName(player.getUsername());
            if (authUser == null) {
                return;
            }

            if (!authUser.isLogged()) {
                player.disconnect(TextUtil.parse(this.messagesService.getMessage("reconnect")));
                return;
            }

            this.proxyServer.getScheduler()
                    .buildTask(ProxyCore.getInstance(), () -> this.authLobbyConnector.findLobbyAndConnect(player))
                    .delay(1, TimeUnit.SECONDS)
                    .schedule();
        }
    }

    @Subscribe
    public void onChoose(PlayerChooseInitialServerEvent event) {
        Player player = event.getPlayer();
        AuthUser authUser = this.authUserCache.findByName(player.getUsername());
        authUser.setConnectionDelay(Instant.now().plus(10, ChronoUnit.SECONDS));
        String hostAddress = player.getRemoteAddress().getAddress().getHostAddress();
        if (authUser.getIpAddress() == null || !authUser.getIpAddress().equals(hostAddress)) {
            authUser.setIpAddress(hostAddress);
        }

        this.authUserRepository.save(authUser);
        event.setInitialServer(this.proxyServer.getServer("auth").get());
        if (authUser.isPremium()) {
            authUser.setLogged(true);
            this.messagesService.getAsText("successfully.logged.in.premium").send(player);
            this.proxyServer.getScheduler()
                    .buildTask(ProxyCore.getInstance(), () -> this.authLobbyConnector.findLobbyAndConnect(player))
                    .delay(1, TimeUnit.SECONDS)
                    .schedule();
        }
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        Player player = event.getPlayer();
        AuthUser authUser = this.authUserCache.findByName(player.getUsername());
        if (authUser == null) {
            return;
        }

        this.authUserRepository.save(authUser);
        this.authUserCache.remove(authUser);
    }

}

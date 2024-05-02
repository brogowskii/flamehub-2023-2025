package io.github.flamehub.proxy.core.auth;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import eu.okaeri.sdk.noproxy.model.NoProxyAddressInfo;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.util.TimeUtil;
import io.github.flamehub.proxy.core.ProxyCore;
import io.github.flamehub.proxy.core.vpn.VPNDetector;
import io.github.flamehub.proxy.core.auth.user.AuthUser;
import io.github.flamehub.proxy.core.auth.user.AuthUserCache;
import io.github.flamehub.proxy.core.auth.user.AuthUserRepository;
import io.github.flamehub.proxy.core.message.VelocityMessagesService;
import io.github.flamehub.proxy.core.util.TextUtil;
import io.github.flamehub.proxy.core.vpn.VPNEntry;
import io.github.flamehub.proxy.core.vpn.VPNEntryRepository;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class AuthListener {


    private final ProxyServer proxyServer;
    private final NetworkPlayerCache networkPlayerCache;
    private final AuthUserCache authUserCache;
    private final AuthUserRepository authUserRepository;
    private final VelocityMessagesService messagesService;
    private final AuthLobbyConnector authLobbyConnector;
    private final VPNEntryRepository vpnEntryRepository;

    public AuthListener(
            ProxyServer proxyServer, NetworkPlayerCache networkPlayerCache,
            AuthUserCache authUserCache,
            AuthUserRepository authUserRepository,
            VelocityMessagesService messagesService,
            AuthLobbyConnector authLobbyConnector,
            VPNEntryRepository vpnEntryRepository
    ) {
        this.proxyServer = proxyServer;
        this.networkPlayerCache = networkPlayerCache;
        this.authUserCache = authUserCache;
        this.authUserRepository = authUserRepository;
        this.messagesService = messagesService;
        this.authLobbyConnector = authLobbyConnector;
        this.vpnEntryRepository = vpnEntryRepository;
    }

    @Subscribe(order = PostOrder.LAST)
    public void onPreLogin(PreLoginEvent event) {
        if (!event.getResult().isAllowed()) {
            return;
        }

        String hostAddress = event.getConnection().getRemoteAddress().getAddress().getHostAddress();
        String name = event.getUsername();
        NetworkPlayer networkPlayer = this.networkPlayerCache.findByName(name);
        if (networkPlayer != null) {
            event.setResult(TextUtil.preDenied(this.messagesService.getMessage("player.already.online")));
            return;
        }

        if (name.length() < 3 || name.length() > 16) {
            event.setResult(TextUtil.preDenied(this.messagesService.getMessage("not.allowed.nickname")));
            return;
        }

        AuthUser authUser = this.authUserCache.findByName(name);
        if (authUser == null) {
            Map.Entry<UUID, Boolean> entry = AuthorizationChecker.getUUID(name);
            UUID uniqueId = entry.getKey();
            AuthUser byUniqueId = this.authUserCache.findByUniqueId(uniqueId);
            if (byUniqueId != null) {
                this.authUserCache.updateName(byUniqueId, name);
                this.authUserRepository.save(byUniqueId);
                authUser = byUniqueId;
            }
            else {

                if (this.authUserCache.findAccountsByIP(hostAddress).size() >= 3) {
                    event.setResult(TextUtil.preDenied(this.messagesService.getMessage("accounts.limit.reached")));
                    return;
                }

                authUser = new AuthUser(uniqueId, name);
                authUser.setPremium(entry.getValue());
                authUser.setFirstIP(hostAddress);
                authUser.setLastIP(hostAddress);
                authUser.getIpHistory().put(hostAddress, new Date());
                authUser.setFirstLoginDate(new Date());
                this.authUserRepository.save(authUser);
            }
        }

        this.authUserCache.add(authUser);
        if (!authUser.isPremium() && !authUser.getName().equals(name)) {
            event.setResult(TextUtil.preDenied(this.messagesService.message("incorrect.nickname")
                    .with("nick", authUser.getName())
                    .applyFirst()));
            return;
        }

        Instant now = Instant.now();
        if (authUser.getConnectionDelay().isAfter(now)) {
            event.setResult(TextUtil.preDenied(this.messagesService.message("connection.delay")
                    .with("time", TimeUtil.formatTimeSimple(Duration.between(now, authUser.getConnectionDelay())))
                    .applyFirst()));
            return;
        }


        event.setResult(authUser.isPremium() ?
                PreLoginEvent.PreLoginComponentResult.forceOnlineMode() :
                PreLoginEvent.PreLoginComponentResult.forceOfflineMode());
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onVPN(LoginEvent event) {
        Player player = event.getPlayer();
        String hostAddress = player.getRemoteAddress().getAddress().getHostAddress();
        VPNEntry vpnEntry = this.vpnEntryRepository.load(hostAddress);
//        if (vpnEntry != null) {
//
//            // To jest po to jakby jakimś cudem to IP, które jest wykryte jako VPN stało się jako dozwolone
//            // Chuj wie czy to jest możliwe, ale wyjebane w to pozdro
//            if (vpnEntry.getExpiration().isBefore(Instant.now())) {
//                NoProxyAddressInfo info = VPNDetector.getInfo(hostAddress);
//                vpnEntry.setBlock(info.getSuggestions().isBlock());
//                vpnEntry.renewExpiration();
//                this.vpnEntryRepository.save(vpnEntry);
//             }
//        }

        if (vpnEntry == null) {
            NoProxyAddressInfo info = VPNDetector.getInfo(hostAddress);
            vpnEntry = new VPNEntry(hostAddress, info.getSuggestions().isBlock());
            this.vpnEntryRepository.save(vpnEntry);
        }

        AuthUser authUser = this.authUserCache.findByName(player.getUsername());
        if (vpnEntry.isBlock() && !authUser.isVpnAllowed()) {
            event.setResult(TextUtil.resultedDenied(this.messagesService.getMessage("vpn.detected")));
        }

    }

    @Subscribe(order = PostOrder.NORMAL)
    public void onLoginEvent(LoginEvent event) {
        Player player = event.getPlayer();
        String hostAddress = player.getRemoteAddress().getAddress().getHostAddress();

        AuthUser authUser = this.authUserCache.findByName(player.getUsername());
        if (authUser.getLastIP() == null || !authUser.getLastIP().equals(hostAddress)) {
            authUser.setAutoLogin(false);
            authUser.setLastIP(hostAddress);
        }

        if (!authUser.getIpHistory().containsKey(hostAddress) && authUser.isPremium()) {
            authUser.getIpHistory().put(hostAddress, new Date());
        }

        if ((authUser.isRegistered() && authUser.isAutoLogin()) || authUser.isPremium()) {
            authUser.setLogged(true);
            this.messagesService.message("successfully.logged.in").send(player);
            this.proxyServer.getScheduler()
                    .buildTask(ProxyCore.getInstance(), () -> this.authLobbyConnector.findLobbyAndConnect(player))
                    .delay(1, TimeUnit.SECONDS)
                    .schedule();
        }

        authUser.setConnectionDelay(Instant.now().plus(5, ChronoUnit.SECONDS));
        authUser.setLastLoginDate(new Date());
        this.authUserRepository.save(authUser);
    }


//    @Subscribe
//    public void onServerConnect(ServerConnectedEvent event) {
//        ServerInfo serverInfo = event.getServer().getServerInfo();
//        Optional<RegisteredServer> previousServer = event.getPreviousServer();
//        if (serverInfo.getName().startsWith("limbo") && previousServer.isPresent()) {
//
//            if (!previousServer.get().getServerInfo().getName().startsWith("lobby")) {
//                return;
//            }
//
//            Player player = event.getPlayer();
//            AuthUser authUser = this.authUserCache.findByName(player.getUsername());
//            if (authUser == null) {
//                return;
//            }
//
//            if (!authUser.isLogged()) {
//                player.disconnect(TextUtil.parse(this.messagesService.getMessage("reconnect")));
//                return;
//            }
//
//            this.proxyServer.getScheduler()
//                    .buildTask(ProxyCore.getInstance(), () -> this.authLobbyConnector.findLobbyAndConnect(player))
//                    .delay(1, TimeUnit.SECONDS)
//                    .schedule();
//        }
//    }

    @Subscribe
    public void onChoose(PlayerChooseInitialServerEvent event) {
        event.setInitialServer(this.proxyServer.getServer("auth").get());
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

    @Subscribe
    public void onCommand(CommandExecuteEvent event) {
        if (event.getCommandSource() instanceof Player player) {
            AuthUser authUser = this.authUserCache.findByName(player.getUsername());
            String command = event.getCommand();

            if (command.equals("lobby")) {

                if (!authUser.isPremium() && !authUser.isLogged()) {
                    event.setResult(CommandExecuteEvent.CommandResult.denied());
                }

            }

        }
    }

}

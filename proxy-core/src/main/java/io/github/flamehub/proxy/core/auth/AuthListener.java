package io.github.flamehub.proxy.core.auth;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import eu.okaeri.sdk.noproxy.model.NoProxyAddressInfo;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.util.TimeUtil;
import io.github.flamehub.proxy.core.ProxyCore;
import io.github.flamehub.proxy.core.ProxyMessages;
import io.github.flamehub.proxy.core.auth.user.AuthUser;
import io.github.flamehub.proxy.core.auth.user.AuthUserCache;
import io.github.flamehub.proxy.core.auth.user.AuthUserRepository;
import io.github.flamehub.proxy.core.util.TextUtil;
import io.github.flamehub.proxy.core.vpn.VPNDetector;
import io.github.flamehub.proxy.core.vpn.VPNEntry;
import io.github.flamehub.proxy.core.vpn.VPNEntryRepository;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class AuthListener {


  private final ProxyCore proxyCore;
  private final ProxyServer proxyServer;
  private final ProxyMessages proxyMessages;
  private final NetworkPlayerCache networkPlayerCache;
  private final AuthUserCache authUserCache;
  private final AuthUserRepository authUserRepository;
  private final AuthLobbyConnector authLobbyConnector;
  private final VPNEntryRepository vpnEntryRepository;

  public AuthListener(
      final ProxyCore proxyCore, final ProxyServer proxyServer,
      final ProxyMessages proxyMessages,
      final NetworkPlayerCache networkPlayerCache,
      final AuthUserCache authUserCache,
      final AuthUserRepository authUserRepository,
      final AuthLobbyConnector authLobbyConnector,
      final VPNEntryRepository vpnEntryRepository
  ) {
    this.proxyCore = proxyCore;
    this.proxyServer = proxyServer;
    this.proxyMessages = proxyMessages;
    this.networkPlayerCache = networkPlayerCache;
    this.authUserCache = authUserCache;
    this.authUserRepository = authUserRepository;
    this.authLobbyConnector = authLobbyConnector;
    this.vpnEntryRepository = vpnEntryRepository;
  }

  @Subscribe(order = PostOrder.LAST)
  public void onPreLogin(final PreLoginEvent event) {
    final PreLoginEvent.PreLoginComponentResult result = event.getResult();
    if (!result.isAllowed()) {
      return;
    }

    final InboundConnection connection = event.getConnection();
    final InetSocketAddress remoteAddress = connection.getRemoteAddress();
    final InetAddress address = remoteAddress.getAddress();
    final String hostAddress = address.getHostAddress();

    final String name = event.getUsername();
    final NetworkPlayer networkPlayer = networkPlayerCache.findByName(name);

    if (networkPlayer != null) {
      event.setResult(TextUtil.preDenied(proxyMessages
          .playerAlreadyOnline
          .applyFirstAsComponent()));
      return;
    }

    if (name.length() < 3 || name.length() > 16) {
      event.setResult(TextUtil.preDenied(proxyMessages
          .notAllowedNickname
          .applyFirstAsComponent()));
      return;
    }

    AuthUser authUser = authUserCache.findByName(name);
    if (authUser == null) {

      final Map.Entry<UUID, Boolean> entry = AuthorizationChecker.getUUID(name);
      final UUID uniqueId = entry.getKey();

      final AuthUser userByUUID = authUserCache.findByUniqueId(uniqueId);

      if (userByUUID != null) {
        authUserCache.updateName(userByUUID, name);
        authUserRepository.save(userByUUID);
        authUser = userByUUID;
      } else {

        if (authUserCache.findAccountsByIP(hostAddress).size() >= 3) {
          event.setResult(TextUtil.preDenied(proxyMessages
              .accountsLimitReached
              .applyFirstAsComponent()));
          return;
        }

        authUser = new AuthUser(uniqueId == null ? UUID.randomUUID() : uniqueId, name);
        authUser.setPremium(entry.getValue());
        authUser.setFirstIP(hostAddress);
        authUser.setLastIP(hostAddress);
        authUser.getIpHistory().put(hostAddress, new Date());
        authUser.setFirstLoginDate(new Date());
        authUserRepository.save(authUser);
      }
    }

    authUserCache.add(authUser);
    if (!authUser.isPremium() && !authUser.getName().equals(name)) {
      event.setResult(TextUtil.preDenied(proxyMessages.incorrectNickname
          .with("nick", authUser.getName())
          .applyFirstAsComponent()));
      return;
    }

    final Instant now = Instant.now();
    if (authUser.getConnectionDelay().isAfter(now)) {
      event.setResult(TextUtil.preDenied(proxyMessages
          .connectionDelay
          .with("time",
              TimeUtil.formatTimeSimple(Duration.between(now, authUser.getConnectionDelay())))
          .applyFirstAsComponent()));
      return;
    }

    event.setResult(authUser.isPremium() ?
        PreLoginEvent.PreLoginComponentResult.forceOnlineMode() :
        PreLoginEvent.PreLoginComponentResult.forceOfflineMode());
  }

  @Subscribe(order = PostOrder.FIRST)
  public void onVPN(final LoginEvent event) {
    final Player player = event.getPlayer();
    final InetSocketAddress remoteAddress = player.getRemoteAddress();
    final InetAddress address = remoteAddress.getAddress();
    final String hostAddress = address.getHostAddress();
    VPNEntry vpnEntry = vpnEntryRepository.load(hostAddress);

    if (vpnEntry == null) {
      final NoProxyAddressInfo info = VPNDetector.getInfo(hostAddress);
      vpnEntry = new VPNEntry(hostAddress, info.getSuggestions().isBlock());
      vpnEntryRepository.save(vpnEntry);
    }

    final AuthUser authUser = authUserCache.findByName(player.getUsername());
    if (vpnEntry.isBlock() && !authUser.isVpnAllowed()) {
      event.setResult(TextUtil.resultedDenied(proxyMessages
          .vpnDetected
          .applyFirstAsComponent()));
    }

  }

  @Subscribe(order = PostOrder.NORMAL)
  public void onLoginEvent(final LoginEvent event) {
    final Player player = event.getPlayer();
    final InetSocketAddress remoteAddress = player.getRemoteAddress();
    final InetAddress address = remoteAddress.getAddress();
    final String hostAddress = address.getHostAddress();

    supplyAsync(() -> authUserCache.findByName(player.getUsername()))
        .thenAccept(context -> {

          authUserCache.update(context.getUniqueId(), entity -> {
            if (entity.getLastIP() == null || !entity.getLastIP().equals(hostAddress)) {
              entity.setAutoLogin(false);
              entity.setLastIP(hostAddress);
            }

            if (!entity.getIpHistory().containsKey(hostAddress) && entity.isPremium()) {
              entity.getIpHistory().put(hostAddress, new Date());
            }

            if ((entity.isRegistered() && entity.isAutoLogin()) || entity.isPremium()) {
              entity.setLogged(true);
              proxyMessages
                  .successfullyLoggedIn
                  .deliver(player);

              proxyServer.getScheduler()
                  .buildTask(proxyCore,
                      () -> authLobbyConnector.findLobbyAndConnect(player))
                  .delay(1, TimeUnit.SECONDS)
                  .schedule();
            }

            entity.setConnectionDelay(Instant.now().plus(5, ChronoUnit.SECONDS));
            entity.setLastLoginDate(new Date());
          });
        });

  }

  @Subscribe
  public void onChoose(final PlayerChooseInitialServerEvent event) {
    event.setInitialServer(proxyServer.getServer("auth").get());
  }

  @Subscribe
  public void onDisconnect(final DisconnectEvent event) {
    final Player player = event.getPlayer();
    final AuthUser authUser = authUserCache.findByName(player.getUsername());
    if (authUser == null) {
      return;
    }

    authUserCache.remove(authUser);
    authUserRepository.save(authUser);

  }

  @Subscribe
  public void onCommand(final CommandExecuteEvent event) {
    if (event.getCommandSource() instanceof final Player player) {
      final AuthUser authUser = authUserCache.findByName(player.getUsername());
      final String command = event.getCommand();

      if ("lobby".equals(command) || "hub".equals(command)) {

        if (!authUser.isPremium() && !authUser.isLogged()) {
          event.setResult(CommandExecuteEvent.CommandResult.denied());
        }

      }

    }
  }

}

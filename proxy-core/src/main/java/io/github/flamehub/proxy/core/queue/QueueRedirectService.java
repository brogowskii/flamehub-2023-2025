package io.github.flamehub.proxy.core.queue;

import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.flamehub.commons.punishment.Punishment;
import io.github.flamehub.commons.punishment.PunishmentMessages;
import io.github.flamehub.commons.punishment.PunishmentRepository;
import io.github.flamehub.commons.punishment.PunishmentType;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.commons.util.TimeUtil;
import io.github.flamehub.proxy.core.util.TextUtil;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import net.kyori.adventure.title.Title;

public final class QueueRedirectService {

  private final ProxyServer proxyServer;
  private final QueueService queueService;
  private final NetworkServerCache networkServerCache;
  private final PunishmentRepository punishmentRepository;
  private final PunishmentMessages punishmentMessages;

  public QueueRedirectService(
      final ProxyServer proxyServer,
      final QueueService queueService,
      final NetworkServerCache networkServerCache,
      final PunishmentRepository punishmentRepository,
      final PunishmentMessages punishmentMessages) {
    this.proxyServer = proxyServer;
    this.queueService = queueService;
    this.networkServerCache = networkServerCache;
    this.punishmentRepository = punishmentRepository;
    this.punishmentMessages = punishmentMessages;
  }

  public boolean move(final String entry, final Queue queue) {
    final Optional<Player> optionalPlayer = proxyServer.getPlayer(entry);
    if (optionalPlayer.isEmpty()) {
      return false;
    }

    final String queueName = queue.getName();
    final Player player = optionalPlayer.get();

    final Punishment punishment = punishmentRepository.isBanned(player.getUsername(),
        player.getRemoteAddress().getAddress().getHostAddress());
    if (!queueName.contains("lobby") && punishment != null) {
      String reason = (punishment.getType() == PunishmentType.BAN_IP ?
          punishmentMessages.banIPKick : punishmentMessages.banKick)
          .with("reason", punishment.getReason())
          .with("admin", punishment.getAdmin())
          .with("time", punishment.getExpireTime() == null ? "Nigdy"
              : TimeUtil.formatTime(Duration.between(Instant.now(), punishment.getExpireTime())))
          .applyFirst();
      player.disconnect(TextUtil.parse(reason));
      return false;
    }

    final NetworkServer leastCrowded = networkServerCache.getLeastCrowded(queueName);
    if (leastCrowded == null) {
      player.showTitle(
          Title.title(TextUtil.parse(""),
              TextUtil.parse("&cBrak serwera w kategorii: &4" + queue.getName()),
              Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(3),
                  Duration.ofSeconds(1))));
      player.sendMessage(TextUtil.parse("&cNie ma żadnego wolnego serwera w kategorii &4" + queue));
      return false;
    }

    if (leastCrowded.isOffline()) {
      player.showTitle(Title.title(TextUtil.parse(""), TextUtil.parse(
              "&cSerwer docelowy &4" + leastCrowded.getName() + " &cjest aktualnie offline!"),
          Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(3), Duration.ofSeconds(1))));
      player.sendMessage(TextUtil.parse(
          "&cSerwer z którym próbujesz się połączyć jest aktualnie &4offline&c! Szukam wolnego serwera..."));
      return false;
    }

    if (leastCrowded.getStatistics().getPlayers() >= leastCrowded.getStatistics()
        .getPlayersLimit()) {
      player.showTitle(Title.title(TextUtil.parse(""), TextUtil.parse(
              "&cSerwer &4" + leastCrowded.getName() + " &cjest prawdopodobnie pełen graczy!"),
          Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(3), Duration.ofSeconds(1))));
      player.sendMessage(TextUtil.parse(
          "&cSerwer z którym próbujesz się połączyć jest prawdopodobnie pełen graczy! Szukam wolnego serwera..."));
      return false;
    }

    proxyServer.getServer(leastCrowded.getName()).ifPresent(registeredServer -> {
      player.createConnectionRequest(registeredServer)
          .connect()
          .whenComplete((result, throwable) -> {
            queue.removeEntry(player.getUsername());

            if (result.getStatus() != ConnectionRequestBuilder.Status.SUCCESS) {
              result.getReasonComponent().ifPresent(player::sendMessage);

              final NetworkServer leastCrowdedLobby = networkServerCache.getLeastCrowded("lobby");
              if (leastCrowdedLobby == null) {
                player.disconnect(TextUtil.parse("&cWystąpił błąd"));
                return;
              }

              player.createConnectionRequest(
                  proxyServer.getServer(leastCrowdedLobby.getName()).get()).fireAndForget();

            }


          });
    });

    return true;
  }

}

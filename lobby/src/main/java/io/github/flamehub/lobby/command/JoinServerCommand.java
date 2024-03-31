package io.github.flamehub.lobby.command;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.redirect.RedirectPacket;
import io.github.flamehub.commons.util.TimeUtil;
import io.github.flamehub.punishment.Punishment;
import io.github.flamehub.punishment.PunishmentRepository;
import io.github.flamehub.punishment.PunishmentType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Command(name = "joinserver", aliases = {"join"})
public final class JoinServerCommand {

    private final Plugin plugin;
    private final RedisMessenger redisMessenger;
    private final BukkitMessagesService messagesService;
    private final NetworkServerCache networkServerCache;
    private final PunishmentRepository punishmentRepository;

    public JoinServerCommand(Plugin plugin, RedisMessenger redisMessenger, BukkitMessagesService messagesService, NetworkServerCache networkServerCache, PunishmentRepository punishmentRepository) {
        this.plugin = plugin;
        this.redisMessenger = redisMessenger;
        this.messagesService = messagesService;
        this.networkServerCache = networkServerCache;
        this.punishmentRepository = punishmentRepository;
    }

    @Execute
    void execute(@Context Player player, @Arg String server) {

        Optional<NetworkServer> optionalNetworkServer = this.networkServerCache.findByName(server);
        optionalNetworkServer.ifPresentOrElse(networkServer -> {

            if (networkServer.isOffline()) {
                this.messagesService.message("server.is.offline")
                        .with("server", networkServer.getName())
                        .send(player);
                return;
            }

            if (networkServer.getStatistics().getPlayers() + 1 > networkServer.getStatistics().getPlayersLimit() & !player.hasPermission("server.join.full")) {
                this.messagesService.message("server.is.full")
                        .with("server", networkServer.getName())
                        .send(player);
                return;
            }

            tryToConnect(player, networkServer);

        }, () -> {

            List<NetworkServer> serversByCategory = this.networkServerCache.findServersByCategory(server);
            if (serversByCategory.isEmpty()) {
                this.messagesService.message("server.and.category.not.found")
                        .with("server", server)
                        .send(player);
                return;
            }

            NetworkServer leastCrowded = this.networkServerCache.getLeastCrowded(server);
            if (leastCrowded == null || leastCrowded.isOffline() || leastCrowded.getStatistics().isFrozen()) {
                this.messagesService.message("unexpected.error.with.least.crowded")
                        .with("server", server)
                        .send(player);
                return;
            }

            tryToConnect(player, leastCrowded);
        });

    }

    void tryToConnect(Player player, NetworkServer networkServer) {
        Punishment banned = punishmentRepository.isBanned(player, player.getAddress().getAddress().getHostAddress(), networkServer.getCategory());
        if (banned != null) {
            String reason = (banned.getType() == PunishmentType.BAN_IP ? this.messagesService.message("punishment.banip.kick") : this.messagesService.message("punishment.ban.kick"))
                    .with("reason", banned.getReason())
                    .with("admin", banned.getAdmin())
                    .with("time", banned.getExpireTime() == null ? "Nigdy" : TimeUtil.formatTime(Duration.between(Instant.now(), banned.getExpireTime())))
                    .applyFirst();
            player.sendMessage(TextUtil.parse(reason));
            return;
        }

        this.messagesService.message("attempt.to.connect.with.server")
                .with("server", networkServer.getName())
                .send(player);
        this.redisMessenger.publish("redirect", new RedirectPacket(player.getName(), networkServer.getName()));
    }


}
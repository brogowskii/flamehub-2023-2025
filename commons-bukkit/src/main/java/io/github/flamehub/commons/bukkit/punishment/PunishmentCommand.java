package io.github.flamehub.commons.bukkit.punishment;

import static io.github.flamehub.commons.util.CompletableFutures.NIL;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.RootCommand;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import io.github.flamehub.commons.punishment.Punishment;
import io.github.flamehub.commons.punishment.PunishmentKickPacket;
import io.github.flamehub.commons.punishment.PunishmentMessages;
import io.github.flamehub.commons.punishment.PunishmentRepository;
import io.github.flamehub.commons.punishment.PunishmentType;
import io.github.flamehub.commons.util.TimeUtil;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RootCommand
public final class PunishmentCommand {

  private final RedisMessenger redisMessenger;
  private final FlameDispatcher flameDispatcher;
  private final PunishmentRepository punishmentRepository;
  private final PunishmentMessages punishmentMessages;
  private final NetworkMessageService networkMessageService;

  public PunishmentCommand(
      final RedisMessenger redisMessenger,
      final FlameDispatcher flameDispatcher,
      final PunishmentRepository punishmentRepository,
      final PunishmentMessages punishmentMessages,
      final NetworkMessageService networkMessageService
  ) {
    this.redisMessenger = redisMessenger;
    this.flameDispatcher = flameDispatcher;
    this.punishmentRepository = punishmentRepository;
    this.punishmentMessages = punishmentMessages;
    this.networkMessageService = networkMessageService;
  }

  @Execute(name = "checkban")
  @Permission("server.commands.checkban")
  CompletableFuture<Void> checkBan(
      final @Context CommandSender sender,
      final @Arg("networkPlayer") String target) {

    return supplyAsync(() -> punishmentRepository.loadAll("punished", target))
        .thenAccept(punishments -> {
          if (punishments.isEmpty()) {
            BukkitMessage.from("&cTen gracz nie posiada żadnych kar na całej sieci!")
                .deliver(sender);
            return;
          }

          BukkitMessage.from("&7Lista kar tego gracza:", "").deliver(sender);
          for (final Punishment punishment : punishments) {

            BukkitMessage
                .from(
                    "&8- &c{type}",
                    " &7Id: &f{id}",
                    " &7Administrator: &f{admin}",
                    " &7Data nadania: &f{date}",
                    " &7Data wygaśnięcia: &f{expire_date}",
                    " &7Powód: &f{reason}",
                    ""
                )
                .with("type", punishment.getType())
                .with("id", punishment.getUniqueId().toString())
                .with("admin", punishment.getAdmin())
                .with("date", TimeUtil.formatDate(punishment.getCreationTime()))
                .with("expire_date", punishment.getExpireTime() == null ? "Nigdy."
                    : TimeUtil.formatDate(punishment.getExpireTime()))
                .with("reason", punishment.getReason())
                .deliver(sender);

          }

        });
  }

  @Execute(name = "mute")
  @Permission("server.commands.mute")
  CompletableFuture<Void> mute(
      final @Context CommandSender sender,
      final @Arg("networkPlayer") String target,
      final @Join String reason) {

    return supplyAsync(() -> punishmentRepository.load(target, PunishmentType.MUTE))
        .thenCompose(punishment -> {

          if (punishment == null) {
            punishment = new Punishment(PunishmentType.MUTE, target, reason, sender.getName(),
                null);
          } else {
            punishment.setAdmin(sender.getName());
            punishment.setReason(reason);
          }

          punishmentRepository.save(punishment);
          return networkMessageService.sendAsync(
              punishmentMessages.muteBroadcast
                  .with("target", target)
                  .with("reason", reason)
                  .with("admin", sender.getName())
                  .apply(),
              NetworkMessageType.CHAT
          );

        });
  }

  @Execute(name = "unmute")
  @Permission("server.commands.unmute")
  CompletableFuture<Void> unMute(
      final @Context CommandSender sender,
      final @Arg("networkPlayer") String target) {

    return supplyAsync(() -> punishmentRepository.load(target, PunishmentType.MUTE))
        .thenCompose(punishment -> {

          if (punishment == null) {
            return supplyAsync(() -> punishmentMessages
                    .punishmentNotFound.apply())
                .thenAccept(messages -> messages.forEach(
                    message -> sender.sendMessage(TextUtil.parse(message))));
          }

          punishmentRepository.delete(punishment);
          return networkMessageService.sendAsync(
              punishmentMessages.unMuteBroadcast
                  .with("target", target)
                  .with("admin", sender.getName())
                  .apply(),
              NetworkMessageType.CHAT
          );

        });

  }

  @Execute(name = "tempmute")
  @Permission("server.commands.tempmute")
  CompletableFuture<Void> tempMute(
      final @Context CommandSender sender,
      final @Arg("networkPlayer") String target,
      final @Arg String duration,
      final @Join String reason) {

    return supplyAsync(() -> punishmentRepository.load(target, PunishmentType.MUTE))
        .thenCompose(punishment -> {

          final Duration formattedDuration = TimeUtil.parseTime(duration);
          final Instant plus = Instant.now().plus(formattedDuration);

          if (punishment == null) {
            punishment = new Punishment(PunishmentType.MUTE, target, reason, sender.getName(),
                plus);
          } else {
            punishment.setAdmin(sender.getName());
            punishment.setReason(reason);
            punishment.setExpireTime(plus);
          }

          punishmentRepository.save(punishment);
          return networkMessageService.sendAsync(
              punishmentMessages.tempMuteBroadcast
                  .with("target", target)
                  .with("reason", reason)
                  .with("admin", sender.getName())
                  .with("duration", TimeUtil.formatTime(formattedDuration))
                  .apply(),
              NetworkMessageType.CHAT
          );

        });
  }

  @Execute(name = "unban")
  @Permission("server.commands.unban")
  CompletableFuture<Void> unban(
      final @Context CommandSender sender,
      final @Arg String target) {

    return supplyAsync(() -> punishmentRepository.load(target, PunishmentType.BAN))
        .thenCompose(punishment -> {

          if (punishment == null) {
            return supplyAsync(() -> punishmentMessages
                    .punishmentNotFound.apply())
                .thenAccept(messages -> messages.forEach(
                    message -> sender.sendMessage(TextUtil.parse(message))));

          }

          punishmentRepository.delete(punishment);
          return networkMessageService.sendAsync(
              punishmentMessages.unbanBroadcast
                  .with("target", target)
                  .with("admin", sender.getName())
                  .apply(),
              NetworkMessageType.CHAT
          );

        });

  }

  @Execute(name = "unbanip")
  @Permission("server.commands.unbanip")
  CompletableFuture<Void> unbanip(
      final @Context CommandSender sender,
      final @Arg String target) {

    return supplyAsync(() -> punishmentRepository.load(target, PunishmentType.BAN_IP))
        .thenCompose(punishment -> {

          if (punishment == null) {
            return supplyAsync(() -> punishmentMessages
                    .punishmentNotFound.apply())
                .thenAccept(messages -> messages.forEach(
                    message -> sender.sendMessage(TextUtil.parse(message))));
          }

          punishmentRepository.delete(punishment);
          return networkMessageService.sendAsync(
              punishmentMessages.unbanBroadcast
                  .with("target", target)
                  .with("admin", sender.getName())
                  .apply(),
              NetworkMessageType.CHAT
          );

        });

  }

  @Execute(name = "ban")
  @Permission("server.commands.ban")
  CompletableFuture<Void> ban(
      final @Context CommandSender sender,
      final @Arg("networkPlayer") String target,
      final @Join String reason) {

    if ("opalkamarcin".equals(target)) {
      return NIL;
    }

    return supplyAsync(() -> punishmentRepository.load(target, PunishmentType.BAN))
        .thenCompose(punishment -> {

          if (punishment == null) {
            punishment = new Punishment(PunishmentType.BAN, target, reason, sender.getName(),
                null);
          } else {
            punishment.setAdmin(sender.getName());
            punishment.setReason(reason);
          }

          punishmentRepository.save(punishment);
          kick(target, punishment);
          return networkMessageService.sendAsync(
              punishmentMessages.banBroadcast
                  .with("target", target)
                  .with("reason", reason)
                  .with("admin", sender.getName())
                  .apply(),
              NetworkMessageType.CHAT
          );

        });
  }

  @Execute(name = "banip")
  @Permission("server.commands.banip")
  CompletableFuture<Void> banIp(
      final @Context CommandSender sender,
      final @Arg("networkPlayer") String target,
      final @Join String reason) {

    if ("opalkamarcin".equals(target)) {
      return NIL;
    }

    return supplyAsync(() -> punishmentRepository.load(target, PunishmentType.BAN_IP))
        .thenCompose(punishment -> {

          if (punishment == null) {
            punishment = new Punishment(PunishmentType.BAN_IP, target, reason, sender.getName(),
                null);
          } else {
            punishment.setAdmin(sender.getName());
            punishment.setReason(reason);
          }

          final Player targetPlayer = Bukkit.getPlayer(target);
          if (targetPlayer != null) {
            final InetSocketAddress address = targetPlayer.getAddress();
            punishment.setPunishedIp(address.getAddress().getHostAddress());
          }

          punishmentRepository.save(punishment);
          kick(target, punishment);
          return networkMessageService.sendAsync(
              punishmentMessages.banIPBroadcast
                  .with("target", target)
                  .with("reason", reason)
                  .with("admin", sender.getName())
                  .apply(),
              NetworkMessageType.CHAT
          );


        });
  }

  @Execute(name = "tempbanip")
  @Permission("server.commands.tempbanip")
  CompletableFuture<Void> tempBanIp(
      final @Context CommandSender sender,
      final @Arg("networkPlayer") String target,
      final @Arg String duration,
      final @Join String reason) {
    if ("opalkamarcin".equals(target)) {
      return NIL;
    }

    return supplyAsync(() -> punishmentRepository.load(target, PunishmentType.BAN_IP))
        .thenCompose(punishment -> {

          final Duration formattedDuration = TimeUtil.parseTime(duration);
          final Instant plus = Instant.now().plus(formattedDuration);

          if (punishment == null) {
            punishment = new Punishment(PunishmentType.BAN_IP, target, reason, sender.getName(),
                plus);
          } else {
            punishment.setAdmin(sender.getName());
            punishment.setReason(reason);
            punishment.setExpireTime(plus);
          }

          final Player targetPlayer = Bukkit.getPlayer(target);
          if (targetPlayer != null) {
            final InetSocketAddress address = targetPlayer.getAddress();
            punishment.setPunishedIp(address.getAddress().getHostAddress());
          }

          kick(target, punishment);
          punishmentRepository.save(punishment);

          return networkMessageService.sendAsync(
              punishmentMessages.tempBanIPBroadcast
                  .with("target", target)
                  .with("reason", reason)
                  .with("admin", sender.getName())
                  .apply(),
              NetworkMessageType.CHAT
          );

        });
  }

  @Execute(name = "tempban")
  @Permission("server.commands.tempban")
  CompletableFuture<Void> tempBan(
      final @Context CommandSender sender,
      final @Arg("networkPlayer") String target,
      final @Arg String duration,
      final @Join String reason) {

    if ("opalkamarcin".equals(target)) {
      return NIL;
    }

    return supplyAsync(() -> punishmentRepository.load(target, PunishmentType.BAN))
        .thenCompose(punishment -> {

          final Duration formattedDuration = TimeUtil.parseTime(duration);
          final Instant plus = Instant.now().plus(formattedDuration);

          if (punishment == null) {
            punishment = new Punishment(PunishmentType.BAN, target, reason, sender.getName(),
                plus);
          } else {
            punishment.setAdmin(sender.getName());
            punishment.setReason(reason);
            punishment.setExpireTime(plus);
          }

          kick(target, punishment);
          punishmentRepository.save(punishment);
          return networkMessageService.sendAsync(
              punishmentMessages.tempBanBroadcast
                  .with("target", target)
                  .with("reason", reason)
                  .with("admin", sender.getName())
                  .with("duration", TimeUtil.formatTime(formattedDuration))
                  .apply(),
              NetworkMessageType.CHAT
          );

        });
  }

  void kick(final String playerName, final Punishment punishment) {

    final String reason = (punishment.getType() == PunishmentType.BAN_IP ?
        punishmentMessages.banIPKick : punishmentMessages.banKick)
        .with("reason", punishment.getReason())
        .with("admin", punishment.getAdmin())
        .with("time", punishment.getExpireTime() == null ? "Nigdy"
            : TimeUtil.formatTime(Duration.between(Instant.now(), punishment.getExpireTime())))
        .applyFirst();

    flameDispatcher.dispatchAsync(() -> redisMessenger.publish("punishments",
        new PunishmentKickPacket(playerName, reason)));

  }


}

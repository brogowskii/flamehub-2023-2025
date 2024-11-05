package io.github.flamehub.commons.bukkit.punishment;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.RootCommand;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import io.github.flamehub.commons.punishment.Punishment;
import io.github.flamehub.commons.punishment.PunishmentKickPacket;
import io.github.flamehub.commons.punishment.PunishmentRepository;
import io.github.flamehub.commons.punishment.PunishmentType;
import io.github.flamehub.commons.util.TimeUtil;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RootCommand
public final class PunishmentCommand {

  private final RedisMessenger redisMessenger;
  private final FlameDispatcher flameDispatcher;
  private final PunishmentRepository punishmentRepository;
  private final BukkitMessagesService messagesService;
  private final NetworkMessageService networkMessageService;

  public PunishmentCommand(
      RedisMessenger redisMessenger,
      FlameDispatcher flameDispatcher,
      PunishmentRepository punishmentRepository,
      BukkitMessagesService messagesService,
      NetworkMessageService networkMessageService
  ) {
    this.redisMessenger = redisMessenger;
    this.flameDispatcher = flameDispatcher;
    this.punishmentRepository = punishmentRepository;
    this.messagesService = messagesService;
    this.networkMessageService = networkMessageService;
  }

  @Execute(name = "checkban")
  @Permission("server.commands.checkban")
  void checkBan(@Context CommandSender sender, @Arg String networkPlayer) {
    this.flameDispatcher.dispatchAsync(() -> {
      List<Punishment> punished = this.punishmentRepository.loadAll("punished", networkPlayer);
      if (punished.isEmpty()) {
        BukkitMessage.from("&cTen gracz nie posiada żadnych kar na całej sieci!").send(sender);
        return;
      }

      BukkitMessage.from("&7Lista kar tego gracza:", "").send(sender);
      for (Punishment punishment : punished) {

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
            .send(sender);

      }

    });
  }

  @Execute(name = "mute")
  @Permission("server.commands.mute")
  void mute(@Context CommandSender sender, @Arg String networkPlayer, @Join String reason) {
    this.flameDispatcher.dispatchAsync(() -> {

      Punishment punishment = this.punishmentRepository.load(networkPlayer, PunishmentType.MUTE);
      if (punishment == null) {
        punishment = new Punishment(PunishmentType.MUTE, networkPlayer, reason, sender.getName(),
            null);
      } else {
        punishment.setAdmin(sender.getName());
        punishment.setReason(reason);
      }

      this.punishmentRepository.save(punishment);
      this.networkMessageService.send(
          this.messagesService.message("punishment.mute.broadcast")
              .with("target", networkPlayer)
              .with("reason", reason)
              .with("admin", sender.getName())
              .apply(),
          NetworkMessageType.CHAT
      );

    });
  }

  @Execute(name = "unmute")
  @Permission("server.commands.unmute")
  void unMute(@Context CommandSender sender, @Arg String networkPlayer) {
    this.flameDispatcher.dispatchAsync(() -> {

      Punishment punishment = this.punishmentRepository.load(networkPlayer, PunishmentType.MUTE);
      if (punishment == null) {
        this.messagesService.message("punishment.not.found").send(sender);
        return;
      }

      this.punishmentRepository.delete(punishment);
      this.networkMessageService.send(
          this.messagesService.message("punishment.unmute.broadcast")
              .with("target", networkPlayer)
              .with("admin", sender.getName())
              .apply(),
          NetworkMessageType.CHAT
      );

    });

  }

  @Execute(name = "tempmute")
  @Permission("server.commands.tempmute")
  void tempMute(@Context CommandSender sender, @Arg String networkPlayer, @Arg String duration,
      @Join String reason) {
    this.flameDispatcher.dispatchAsync(() -> {

      Duration formattedDuration = TimeUtil.parseTime(duration);
      Instant plus = Instant.now().plus(formattedDuration);

      Punishment punishment = this.punishmentRepository.load(networkPlayer, PunishmentType.MUTE);
      if (punishment == null) {
        punishment = new Punishment(PunishmentType.MUTE, networkPlayer, reason, sender.getName(),
            plus);
      } else {
        punishment.setAdmin(sender.getName());
        punishment.setReason(reason);
        punishment.setExpireTime(plus);
      }

      this.punishmentRepository.save(punishment);
      this.networkMessageService.send(
          this.messagesService.message("punishment.tempmute.broadcast")
              .with("target", networkPlayer)
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
  void unban(@Context CommandSender sender, @Arg String name) {
    this.flameDispatcher.dispatchAsync(() -> {

      Punishment punishment = this.punishmentRepository.load(name, PunishmentType.BAN);
      if (punishment == null) {
        this.messagesService.message("punishment.not.found").send(sender);
        return;
      }

      this.punishmentRepository.delete(punishment);
      this.networkMessageService.send(
          this.messagesService.message("punishment.unban.broadcast")
              .with("target", name)
              .with("admin", sender.getName())
              .apply(),
          NetworkMessageType.CHAT
      );

    });

  }

  @Execute(name = "unbanip")
  @Permission("server.commands.unbanip")
  void unbanip(@Context CommandSender sender, @Arg String name) {
    this.flameDispatcher.dispatchAsync(() -> {

      Punishment punishment = this.punishmentRepository.load(name, PunishmentType.BAN_IP);
      if (punishment == null) {
        this.messagesService.message("punishment.not.found").send(sender);
        return;
      }

      this.punishmentRepository.delete(punishment);
      this.networkMessageService.send(
          this.messagesService.message("punishment.unban.broadcast")
              .with("target", name)
              .with("admin", sender.getName())
              .apply(),
          NetworkMessageType.CHAT
      );

    });

  }

  @Execute(name = "ban")
  @Permission("server.commands.ban")
  void ban(@Context CommandSender sender, @Arg String networkPlayer, @Join String reason) {
    if (networkPlayer.equals("opalkamarcin")) {
      return;
    }
    this.flameDispatcher.dispatchAsync(() -> {

      Punishment punishment = this.punishmentRepository.load(networkPlayer, PunishmentType.BAN);
      if (punishment == null) {
        punishment = new Punishment(PunishmentType.BAN, networkPlayer, reason, sender.getName(),
            null);
      } else {
        punishment.setAdmin(sender.getName());
        punishment.setReason(reason);
      }

      this.punishmentRepository.save(punishment);
      this.networkMessageService.send(
          this.messagesService.message("punishment.ban.broadcast")
              .with("target", networkPlayer)
              .with("reason", reason)
              .with("admin", sender.getName())
              .apply(),
          NetworkMessageType.CHAT
      );

      kick(networkPlayer, punishment);

    });
  }

  @Execute(name = "banip")
  @Permission("server.commands.banip")
  void banIp(@Context CommandSender sender, @Arg String networkPlayer, @Join String reason) {
    if (networkPlayer.equals("opalkamarcin")) {
      return;
    }
    this.flameDispatcher.dispatchAsync(() -> {

      Punishment punishment = this.punishmentRepository.load(networkPlayer, PunishmentType.BAN_IP);
      if (punishment == null) {
        punishment = new Punishment(PunishmentType.BAN_IP, networkPlayer, reason, sender.getName(),
            null);
      } else {
        punishment.setAdmin(sender.getName());
        punishment.setReason(reason);
      }

      Player target = Bukkit.getPlayer(networkPlayer);
      if (target != null) {
        punishment.setPunishedIp(target.getAddress().getAddress().getHostAddress());
      }

      this.punishmentRepository.save(punishment);
      this.networkMessageService.send(
          this.messagesService.message("punishment.banip.broadcast")
              .with("target", networkPlayer)
              .with("reason", reason)
              .with("admin", sender.getName())
              .apply(),
          NetworkMessageType.CHAT
      );

      kick(networkPlayer, punishment);
    });
  }

  @Execute(name = "tempbanip")
  @Permission("server.commands.tempbanip")
  void tempBanIp(@Context CommandSender sender, @Arg String networkPlayer, @Arg String duration,
      @Join String reason) {
    if (networkPlayer.equals("opalkamarcin")) {
      return;
    }
    this.flameDispatcher.dispatchAsync(() -> {

      Duration formattedDuration = TimeUtil.parseTime(duration);
      Instant plus = Instant.now().plus(formattedDuration);
      Punishment punishment = this.punishmentRepository.load(networkPlayer, PunishmentType.BAN_IP);
      if (punishment == null) {
        punishment = new Punishment(PunishmentType.BAN_IP, networkPlayer, reason, sender.getName(),
            plus);
      } else {
        punishment.setAdmin(sender.getName());
        punishment.setReason(reason);
        punishment.setExpireTime(plus);
      }

      Player target = Bukkit.getPlayer(networkPlayer);
      if (target != null) {
        punishment.setPunishedIp(target.getAddress().getAddress().getHostAddress());
      }
      this.punishmentRepository.save(punishment);
      this.networkMessageService.send(
          this.messagesService.message("punishment.tempbanip.broadcast")
              .with("target", networkPlayer)
              .with("reason", reason)
              .with("admin", sender.getName())
              .apply(),
          NetworkMessageType.CHAT
      );

      kick(networkPlayer, punishment);

    });
  }

  @Execute(name = "tempban")
  @Permission("server.commands.tempban")
  void tempBan(@Context CommandSender sender, @Arg String networkPlayer, @Arg String duration,
      @Join String reason) {
    if (networkPlayer.equals("opalkamarcin")) {
      return;
    }

    this.flameDispatcher.dispatchAsync(() -> {

      Duration formattedDuration = TimeUtil.parseTime(duration);
      Instant plus = Instant.now().plus(formattedDuration);

      Punishment punishment = this.punishmentRepository.load(networkPlayer, PunishmentType.BAN);
      if (punishment == null) {
        punishment = new Punishment(PunishmentType.BAN, networkPlayer, reason, sender.getName(),
            plus);
      } else {
        punishment.setAdmin(sender.getName());
        punishment.setReason(reason);
        punishment.setExpireTime(plus);
      }

      this.punishmentRepository.save(punishment);
      this.networkMessageService.send(
          this.messagesService.message("punishment.tempban.broadcast")
              .with("target", networkPlayer)
              .with("reason", reason)
              .with("admin", sender.getName())
              .with("duration", TimeUtil.formatTime(formattedDuration))
              .apply(),
          NetworkMessageType.CHAT
      );

      kick(networkPlayer, punishment);

    });
  }

  void kick(String playerName, Punishment punishment) {

    String reason = (punishment.getType() == PunishmentType.BAN_IP ? this.messagesService.message(
        "punishment.banip.kick") : this.messagesService.message("punishment.ban.kick"))
        .with("reason", punishment.getReason())
        .with("admin", punishment.getAdmin())
        .with("time", punishment.getExpireTime() == null ? "Nigdy"
            : TimeUtil.formatTime(Duration.between(Instant.now(), punishment.getExpireTime())))
        .applyFirst();
    this.flameDispatcher.dispatch(() -> this.redisMessenger.publish("punishments",
        new PunishmentKickPacket(playerName, reason)));

  }


}

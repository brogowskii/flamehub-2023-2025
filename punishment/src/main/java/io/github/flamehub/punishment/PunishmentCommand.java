package io.github.flamehub.punishment;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.RootCommand;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.commons.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@RootCommand
public final class PunishmentCommand {

    private final FlameDispatcher flameDispatcher;
    private final PunishmentRepository punishmentRepository;
    private final BukkitMessagesService messagesService;
    private final NetworkMessageService networkMessageService;
    private final NetworkServerCache networkServerCache;

    public PunishmentCommand(FlameDispatcher flameDispatcher, PunishmentRepository punishmentRepository, BukkitMessagesService messagesService, NetworkMessageService networkMessageService, NetworkServerCache networkServerCache) {
        this.flameDispatcher = flameDispatcher;
        this.punishmentRepository = punishmentRepository;
        this.messagesService = messagesService;
        this.networkMessageService = networkMessageService;
        this.networkServerCache = networkServerCache;
    }

    @Execute(name = "checkban")
    @Permission("server.commands.checkban")
    void checkBan(@Context CommandSender sender, @Arg String playerName) {
        this.flameDispatcher.dispatchAsync(() -> {
            List<Punishment> punished = this.punishmentRepository.loadAll("punished", playerName);
            if (punished.isEmpty()) {
                BukkitMessage.from("&cTen gracz nie posiada żadnych kar na całej sieci!").send(sender);
                return;
            }

            BukkitMessage.from("&7Lista kar tego gracza na całej sieci:", "").send(sender);
            for (Punishment punishment : punished) {

                BukkitMessage
                        .from(
                                "&8- &c{type} &8-> &c{category}",
                                " &7Id: &f{id}",
                                " &7Administrator: &f{admin}",
                                " &7Data nadania: &f{date}",
                                " &7Data wygaśnięcia: &f{expire_date}",
                                " &7Powód: &f{reason}",
                                ""
                        )
                        .with("type", punishment.getType())
                        .with("category", punishment.getServerCategory())
                        .with("id", punishment.getUniqueId().toString())
                        .with("admin", punishment.getAdmin())
                        .with("date", TimeUtil.formatDate(punishment.getCreationTime()))
                        .with("expire_date", punishment.getExpireTime() == null ? "Nigdy." : TimeUtil.formatDate(punishment.getExpireTime()))
                        .with("reason", punishment.getReason())
                        .send(sender);

            }

        });
    }

    @Execute(name = "mute")
    @Permission("server.commands.mute")
    void mute(@Context CommandSender sender, @Arg String playerName, @Join String reason) {
        this.flameDispatcher.dispatchAsync(() -> {

            String category = this.networkServerCache.getCurrent().getCategory();
            Punishment punishment = this.punishmentRepository.load(playerName, category, PunishmentType.MUTE);
            if (punishment == null) {
                punishment = new Punishment(PunishmentType.MUTE, playerName, category, reason, sender.getName(), null);
            }
            else {
                punishment.setAdmin(sender.getName());
                punishment.setReason(reason);
            }

            this.punishmentRepository.save(punishment);
            this.networkMessageService.send(
                    this.messagesService.message("punishment.mute.broadcast")
                            .with("target", playerName)
                            .with("reason", reason)
                            .with("admin", sender.getName())
                            .apply(),
                    NetworkMessageType.CHAT
            );

        });
    }

    @Execute(name = "unmute")
    @Permission("server.commands.unmute")
    void unMute(@Context CommandSender sender, @Arg String playerName) {
        this.flameDispatcher.dispatchAsync(() -> {

            String category = this.networkServerCache.getCurrent().getCategory();
            Punishment punishment = this.punishmentRepository.load(playerName, category, PunishmentType.MUTE);
            if (punishment == null) {
                this.messagesService.message("punishment.not.found").send(sender);
                return;
            }

            this.punishmentRepository.delete(punishment);
            this.networkMessageService.send(
                    this.messagesService.message("punishment.unmute.broadcast")
                            .with("target", playerName)
                            .with("admin", sender.getName())
                            .apply(),
                    NetworkMessageType.CHAT
            );

        });

    }

    @Execute(name = "tempmute")
    @Permission("server.commands.tempmute")
    void tempMute(@Context CommandSender sender, @Arg String playerName, @Arg String duration, @Join String reason) {
        this.flameDispatcher.dispatchAsync(() -> {

            String category = this.networkServerCache.getCurrent().getCategory();
            Duration formattedDuration = TimeUtil.parseTime(duration);
            Instant plus = Instant.now().plus(formattedDuration);

            Punishment punishment = this.punishmentRepository.load(playerName, category, PunishmentType.MUTE);
            if (punishment == null) {
                punishment = new Punishment(PunishmentType.MUTE, playerName, category, reason, sender.getName(), plus);
            }
            else {
                punishment.setAdmin(sender.getName());
                punishment.setReason(reason);
                punishment.setExpireTime(plus);
            }

            this.punishmentRepository.save(punishment);
            this.networkMessageService.send(
                    this.messagesService.message("punishment.tempmute.broadcast")
                            .with("target", playerName)
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

            String category = this.networkServerCache.getCurrent().getCategory();
            Punishment punishment = this.punishmentRepository.load(name, category, PunishmentType.BAN);
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

            String category = this.networkServerCache.getCurrent().getCategory();
            Punishment punishment = this.punishmentRepository.load(name, category, PunishmentType.BAN_IP);
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
    void ban(@Context CommandSender sender, @Arg String playerName, @Join String reason) {
        this.flameDispatcher.dispatchAsync(() -> {

            String category = this.networkServerCache.getCurrent().getCategory();
            Punishment punishment = this.punishmentRepository.load(playerName, category, PunishmentType.BAN);
            if (punishment == null) {
                punishment = new Punishment(PunishmentType.BAN, playerName, category, reason, sender.getName(), null);
            }
            else {
                punishment.setAdmin(sender.getName());
                punishment.setReason(reason);
            }

            this.punishmentRepository.save(punishment);
            this.networkMessageService.send(
                    this.messagesService.message("punishment.ban.broadcast")
                            .with("target", playerName)
                            .with("reason", reason)
                            .with("admin", sender.getName())
                            .apply(),
                    NetworkMessageType.CHAT
            );

            kick(playerName, punishment);

        });
    }

    @Execute(name = "banip")
    @Permission("server.commands.banip")
    void banIp(@Context CommandSender sender, @Arg String playerName, @Join String reason) {
        this.flameDispatcher.dispatchAsync(() -> {

            String category = this.networkServerCache.getCurrent().getCategory();
            Punishment punishment = this.punishmentRepository.load(playerName, category, PunishmentType.BAN_IP);
            if (punishment == null) {
                punishment = new Punishment(PunishmentType.BAN_IP, playerName, category, reason, sender.getName(), null);
            }
            else {
                punishment.setAdmin(sender.getName());
                punishment.setReason(reason);
            }

            Player target = Bukkit.getPlayer(playerName);
            if (target != null) {
                punishment.setPunishedIp(target.getAddress().getAddress().getHostAddress());
            }

            this.punishmentRepository.save(punishment);
            this.networkMessageService.send(
                    this.messagesService.message("punishment.banip.broadcast")
                            .with("target", playerName)
                            .with("reason", reason)
                            .with("admin", sender.getName())
                            .apply(),
                    NetworkMessageType.CHAT
            );


            kick(playerName, punishment);
        });
    }

    @Execute(name = "tempbanip")
    @Permission("server.commands.tempbanip")
    void tempBanIp(@Context CommandSender sender, @Arg String playerName, @Arg String duration, @Join String reason) {
        this.flameDispatcher.dispatchAsync(() -> {

            String category = this.networkServerCache.getCurrent().getCategory();
            Duration formattedDuration = TimeUtil.parseTime(duration);
            Instant plus = Instant.now().plus(formattedDuration);
            Punishment punishment = this.punishmentRepository.load(playerName, category, PunishmentType.BAN_IP);
            if (punishment == null) {
                punishment = new Punishment(PunishmentType.BAN_IP, playerName, category, reason, sender.getName(), plus);
            }
            else {
                punishment.setAdmin(sender.getName());
                punishment.setReason(reason);
                punishment.setExpireTime(plus);
            }

            Player target = Bukkit.getPlayer(playerName);
            if (target != null) {
                punishment.setPunishedIp(target.getAddress().getAddress().getHostAddress());
            }
            this.punishmentRepository.save(punishment);
            this.networkMessageService.send(
                    this.messagesService.message("punishment.tempbanip.broadcast")
                            .with("target", playerName)
                            .with("reason", reason)
                            .with("admin", sender.getName())
                            .apply(),
                    NetworkMessageType.CHAT
            );

            kick(playerName, punishment);

        });
    }

    @Execute(name = "tempban")
    @Permission("server.commands.tempban")
    void tempBan(@Context CommandSender sender, @Arg String playerName, @Arg String duration, @Join String reason) {
        this.flameDispatcher.dispatchAsync(() -> {

            String category = this.networkServerCache.getCurrent().getCategory();
            Duration formattedDuration = TimeUtil.parseTime(duration);
            Instant plus = Instant.now().plus(formattedDuration);

            Punishment punishment = this.punishmentRepository.load(playerName, category, PunishmentType.BAN);
            if (punishment == null) {
                punishment = new Punishment(PunishmentType.BAN, playerName, category, reason, sender.getName(), plus);
            }
            else {
                punishment.setAdmin(sender.getName());
                punishment.setReason(reason);
                punishment.setExpireTime(plus);
            }

            this.punishmentRepository.save(punishment);
            this.networkMessageService.send(
                    this.messagesService.message("punishment.tempban.broadcast")
                            .with("target", playerName)
                            .with("reason", reason)
                            .with("admin", sender.getName())
                            .with("duration", TimeUtil.formatTime(formattedDuration))
                            .apply(),
                    NetworkMessageType.CHAT
            );

            kick(playerName, punishment);

        });
    }

    void kick(String playerName, Punishment punishment) {
        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            return;
        }

        String reason = (punishment.getType() == PunishmentType.BAN_IP ? this.messagesService.message("punishment.banip.kick") : this.messagesService.message("punishment.ban.kick"))
                .with("reason", punishment.getReason())
                .with("admin", punishment.getAdmin())
                .with("time", punishment.getExpireTime() == null ? "Nigdy" : TimeUtil.formatTime(Duration.between(Instant.now(), punishment.getExpireTime())))
                .applyFirst();
        this.flameDispatcher.dispatch(() -> player.kick(TextUtil.parse(reason)));

    }


}

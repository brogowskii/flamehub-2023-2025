package io.github.flamehub.punishment;

import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.commons.util.TimeUtil;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.Duration;
import java.time.Instant;

public final class PunishmentListener implements Listener {

    private final BukkitMessagesService messagesService;
    private final PunishmentRepository punishmentRepository;
    private final NetworkServerCache networkServerCache;

    public PunishmentListener(BukkitMessagesService messagesService, PunishmentRepository punishmentRepository, NetworkServerCache networkServerCache) {
        this.messagesService = messagesService;
        this.punishmentRepository = punishmentRepository;
        this.networkServerCache = networkServerCache;
    }

//    @EventHandler(priority = EventPriority.HIGHEST)
//    public void onLogin(AsyncPlayerPreLoginEvent event) {
//        AsyncPlayerPreLoginEvent.Result loginResult = event.getLoginResult();
//        if (loginResult != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
//            return;
//        }
//
//        String category = this.networkServerCache.getCurrent().getCategory();
//        String hostAddress = event.getAddress().getHostAddress();
//        Punishment punishment = this.punishmentRepository.loadByIp(hostAddress, category, PunishmentType.BAN_IP);
//        if (punishment == null) {
//            punishment = this.punishmentRepository.load(event.getName(), category, PunishmentType.BAN_IP);
//        }
//        if (punishment != null) {
//            punishment.setPunishedIp(hostAddress);
//        }
//        else {
//            punishment = this.punishmentRepository.load(event.getName(), category, PunishmentType.BAN);
//        }
//
//        if (punishment != null) {
//            check(punishment, event);
//        }
//
//    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        String category = this.networkServerCache.getCurrent().getCategory();
        Player player = event.getPlayer();
        Punishment punishment = this.punishmentRepository.load(player.getName(), category, PunishmentType.MUTE);
        if (punishment == null) {
            return;
        }

        if (punishment.getExpireTime() != null && punishment.isExpired()) {
            this.punishmentRepository.delete(punishment);
            return;
        }

        event.setCancelled(true);
        this.messagesService.message("punishment.mute")
                .with("reason", punishment.getReason())
                .with("admin", punishment.getAdmin())
                .with("time", punishment.getExpireTime() == null ? "Nigdy" : TimeUtil.formatTime(Duration.between(Instant.now(), punishment.getExpireTime())))
                .send(player);
    }

//    void check(Punishment punishment, AsyncPlayerPreLoginEvent event) {
//        if (punishment.getExpireTime() != null && punishment.isExpired()) {
//            this.punishmentRepository.delete(punishment);
//            return;
//        }
//
//        String reason = (punishment.getType() == PunishmentType.BAN_IP ? this.messagesService.message("punishment.banip.kick") : this.messagesService.message("punishment.ban.kick"))
//                .with("reason", punishment.getReason())
//                .with("admin", punishment.getAdmin())
//                .with("time", punishment.getExpireTime() == null ? "Nigdy" : TimeUtil.formatTime(Duration.between(Instant.now(), punishment.getExpireTime())))
//                .applyFirst();
//        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, TextUtil.parse(reason));
//    }

}

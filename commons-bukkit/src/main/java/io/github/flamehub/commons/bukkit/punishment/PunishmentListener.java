package io.github.flamehub.commons.bukkit.punishment;

import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.punishment.Punishment;
import io.github.flamehub.commons.punishment.PunishmentRepository;
import io.github.flamehub.commons.punishment.PunishmentType;
import io.github.flamehub.commons.util.TimeUtil;
import io.papermc.paper.event.player.AsyncChatEvent;
import java.time.Duration;
import java.time.Instant;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public final class PunishmentListener implements Listener {

  private final PunishmentRepository punishmentRepository;
  private final BukkitMessagesService messagesService;

  public PunishmentListener(PunishmentRepository punishmentRepository,
      BukkitMessagesService messagesService) {
    this.punishmentRepository = punishmentRepository;
    this.messagesService = messagesService;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onChat(AsyncChatEvent event) {
    Player player = event.getPlayer();
    Punishment punishment = this.punishmentRepository.load(player.getName(), PunishmentType.MUTE);
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
        .with("time", punishment.getExpireTime() == null ? "Nigdy"
            : TimeUtil.formatTime(Duration.between(Instant.now(), punishment.getExpireTime())))
        .send(player);
  }

}

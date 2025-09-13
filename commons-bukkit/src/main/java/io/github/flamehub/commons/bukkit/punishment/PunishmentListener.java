package io.github.flamehub.commons.bukkit.punishment;

import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.punishment.Punishment;
import io.github.flamehub.commons.punishment.PunishmentMessages;
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
  private final PunishmentMessages punishmentMessages;

  public PunishmentListener(
      final PunishmentRepository punishmentRepository,
      final PunishmentMessages punishmentMessages) {
    this.punishmentRepository = punishmentRepository;
    this.punishmentMessages = punishmentMessages;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onChat(final AsyncChatEvent event) {
    final Player player = event.getPlayer();
    final Punishment punishment = punishmentRepository.load(player.getName(), PunishmentType.MUTE);
    if (punishment == null) {
      return;
    }

    if (punishment.getExpireTime() != null && punishment.isExpired()) {
      punishmentRepository.delete(punishment);
      return;
    }

    event.setCancelled(true);
    BukkitMessage.from(punishmentMessages.mutedInfo
            .with("reason", punishment.getReason())
            .with("admin", punishment.getAdmin())
            .with("time", punishment.getExpireTime() == null ? "Nigdy"
                : TimeUtil.formatTime(Duration.between(Instant.now(), punishment.getExpireTime())))
            .apply())
        .deliverAsync(player);


  }

}

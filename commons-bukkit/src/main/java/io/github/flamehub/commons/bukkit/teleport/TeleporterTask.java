package io.github.flamehub.commons.bukkit.teleport;

import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.util.TitleUtil;
import io.github.flamehub.commons.util.TimeUtil;
import java.time.Duration;
import java.time.Instant;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class TeleporterTask implements Runnable {

  private final TeleporterService teleporterService;
  private final BukkitMessagesService bukkitmessagesService;

  public TeleporterTask(TeleporterService teleporterService,
      BukkitMessagesService bukkitmessagesService) {
    this.teleporterService = teleporterService;
    this.bukkitmessagesService = bukkitmessagesService;
  }

  @Override
  public void run() {

    Instant now = Instant.now();
    for (Teleporter value : teleporterService.values()) {

      Player player = Bukkit.getPlayer(value.getUniqueId());
      if (player == null) {
        teleporterService.remove(value);
        continue;
      }

      Location startLocation = value.getStartLocation();
      if (player.getLocation().getWorld() != startLocation.getWorld()) {
        player.teleportAsync(value.getTargetLocation());
        teleporterService.remove(value);
        TitleUtil.title(player,
            bukkitmessagesService.getMessage("teleportation.success.title"),
            bukkitmessagesService.getMessage("teleportation.success.subtitle"),
            0, 20, 10
        );
        continue;

      }

      if (player.getLocation().distance(startLocation) > 0.5) {
        teleporterService.remove(value);
        TitleUtil.title(player,
            bukkitmessagesService.getMessage("move.detected.during.teleportation.title"),
            bukkitmessagesService.getMessage("move.detected.during.teleportation.subtitle"),
            0, 20, 10
        );
        continue;
      }

      Instant teleportTime = value.getTeleportTime();
      if (now.isBefore(teleportTime)) {
        String formatTime = TimeUtil.formatTimeSimple(Duration.between(now, teleportTime));
        TitleUtil.title(
            player,
            bukkitmessagesService.getMessage("teleportation.timer.title"),
            bukkitmessagesService.getMessage("teleportation.timer.subtitle")
                .replace("{TIME}", formatTime),
            0, 20, 10
        );
        continue;
      }

      player.teleportAsync(value.getTargetLocation());
      teleporterService.remove(value);
      TitleUtil.title(player,
          bukkitmessagesService.getMessage("teleportation.success.title"),
          bukkitmessagesService.getMessage("teleportation.success.subtitle"),
          0, 20, 10
      );

    }

  }
}

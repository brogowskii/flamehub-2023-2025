package io.github.flamehub.commons.bukkit.teleport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.util.TitleUtil;
import io.github.flamehub.commons.util.TimeUtil;

import java.time.Duration;
import java.time.Instant;

public final class TeleporterTask implements Runnable {

    private final TeleporterService teleporterService;
    private final BukkitMessagesService bukkitmessagesService;

    public TeleporterTask(TeleporterService teleporterService, BukkitMessagesService bukkitmessagesService) {
        this.teleporterService = teleporterService;
        this.bukkitmessagesService = bukkitmessagesService;
    }

    @Override
    public void run() {

        Instant now = Instant.now();
        for (Teleporter value : this.teleporterService.values()) {

            Player player = Bukkit.getPlayer(value.getUniqueId());
            if (player == null) {
                this.teleporterService.remove(value);
                continue;
            }

            Location startLocation = value.getStartLocation();
            if (player.getLocation().getWorld() != startLocation.getWorld()) {
                player.teleportAsync(value.getTargetLocation());
                this.teleporterService.remove(value);
                TitleUtil.title(player,
                        this.bukkitmessagesService.getMessage("teleportation.success.title"),
                        this.bukkitmessagesService.getMessage("teleportation.success.subtitle"),
                        0, 20, 10
                );
                continue;

            }

            if (player.getLocation().distance(startLocation) > 0.5) {
                this.teleporterService.remove(value);
                TitleUtil.title(player,
                        this.bukkitmessagesService.getMessage("move.detected.during.teleportation.title"),
                        this.bukkitmessagesService.getMessage("move.detected.during.teleportation.subtitle"),
                        0, 20, 10
                );
                continue;
            }

            Instant teleportTime = value.getTeleportTime();
            if (now.isBefore(teleportTime)) {
                String formatTime = TimeUtil.formatTimeSimple(Duration.between(now, teleportTime));
                TitleUtil.title(
                        player,
                        this.bukkitmessagesService.getMessage("teleportation.timer.title"),
                        this.bukkitmessagesService.getMessage("teleportation.timer.subtitle")
                                .replace("{TIME}", formatTime),
                        0, 20, 10
                );
                continue;
            }

            player.teleportAsync(value.getTargetLocation());
            this.teleporterService.remove(value);
            TitleUtil.title(player,
                    this.bukkitmessagesService.getMessage("teleportation.success.title"),
                    this.bukkitmessagesService.getMessage("teleportation.success.subtitle"),
                    0, 20, 10
            );

        }

    }
}

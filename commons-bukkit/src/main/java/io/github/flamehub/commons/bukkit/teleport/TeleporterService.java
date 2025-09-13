package io.github.flamehub.commons.bukkit.teleport;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class TeleporterService {

  private final Map<UUID, Teleporter> teleportMap = new ConcurrentHashMap<>();

  public void teleport(final Player player, final Location target, final int seconds) {
    if (player == null) {
      return;
    }

    if (seconds <= 0L || player.hasPermission("teleport.cooldown.bypass")) {
      player.teleport(target);
      return;
    }

    final Teleporter teleporter = new Teleporter(
        player.getUniqueId(),
        player.getLocation().clone(),
        target.clone(),
        Instant.now().plus(seconds, ChronoUnit.SECONDS)
    );

    add(teleporter);
  }

  public void add(final Teleporter teleport) {
    teleportMap.put(teleport.getUniqueId(), teleport);
  }

  public void remove(final Teleporter teleport) {
    teleportMap.remove(teleport.getUniqueId());
  }

  public Teleporter findByUniqueId(final UUID uniqueId) {
    return teleportMap.get(uniqueId);
  }

  public Collection<Teleporter> values() {
    return teleportMap.values();
  }

}

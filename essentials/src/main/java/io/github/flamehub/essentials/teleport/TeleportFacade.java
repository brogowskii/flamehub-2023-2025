package io.github.flamehub.essentials.teleport;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class TeleportFacade {

  final Map<UUID, String> teleportMap = new ConcurrentHashMap<>();

  void add(UUID uuid, String target) {
    this.teleportMap.put(uuid, target);
  }

  String getTeleportTarget(UUID uuid) {
    return this.teleportMap.get(uuid);
  }

  void remove(UUID uuid) {
    this.teleportMap.remove(uuid);
  }

}

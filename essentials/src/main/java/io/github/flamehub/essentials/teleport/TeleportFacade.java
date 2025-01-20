package io.github.flamehub.essentials.teleport;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class TeleportFacade {

  final Map<UUID, String> teleportMap = new ConcurrentHashMap<>();

  void add(UUID uuid, String target) {
    teleportMap.put(uuid, target);
  }

  String getTeleportTarget(UUID uuid) {
    return teleportMap.get(uuid);
  }

  void remove(UUID uuid) {
    teleportMap.remove(uuid);
  }

}

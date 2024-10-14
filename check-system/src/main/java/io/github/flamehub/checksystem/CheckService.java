package io.github.flamehub.checksystem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class CheckService {

  private final Map<UUID, Long> checkCooldownMap = new HashMap<>();
  private final Set<Check> checkingPlayers = new HashSet<>();

  public void addCooldown(UUID uniqueId, long millis) {
    this.checkCooldownMap.put(uniqueId, System.currentTimeMillis() + millis);
  }

  public long getCooldown(UUID uniqueId) {
    return this.checkCooldownMap.getOrDefault(uniqueId, System.currentTimeMillis());
  }

  public boolean contains(UUID uniqueId) {
    for (Check check : checkingPlayers) {
      if (check.getPlayer() == uniqueId) {
        return true;
      }
    }
    return false;
  }

  public Check getCheck(UUID uniqueId) {
    for (Check check : checkingPlayers) {
      if (check.getPlayer() == uniqueId) {
        return check;
      }
    }
    return null;
  }

  public void add(Check check) {
    this.checkingPlayers.add(check);
  }

  public void remove(UUID uniqueId) {
    Check check = getCheck(uniqueId);
    this.checkingPlayers.remove(check);
  }

}

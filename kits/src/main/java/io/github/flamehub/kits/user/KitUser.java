package io.github.flamehub.kits.user;

import dev.morphia.annotations.Entity;
import io.github.flamehub.commons.user.User;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity("kit_users")
public final class KitUser extends User {

  private final Map<String, Instant> kitCooldownMap = new HashMap<>();

  private KitUser() {

  }

  public KitUser(final UUID uniqueId, final String name) {
    super(uniqueId, name);
  }

  public void addCooldown(final String kitName, final Instant instant) {
    kitCooldownMap.put(kitName, instant);
  }

  public Instant getKitCooldown(final String kitName) {
    return kitCooldownMap.getOrDefault(kitName, Instant.ofEpochMilli(0));
  }


}

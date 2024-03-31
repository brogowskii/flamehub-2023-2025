package io.github.flamehub.kits.user;

import dev.morphia.annotations.Entity;
import io.github.flamehub.commons.user.User;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity("kit_users")
public final class KitUser extends User {

    private Map<String, Instant> kitCooldownMap = new HashMap<>();

    private KitUser() {

    }

    public KitUser(UUID uniqueId, String name) {
        super(uniqueId, name);
    }

    public void addCooldown(String kitName, Instant instant) {
        this.kitCooldownMap.put(kitName, instant);
    }

    public Instant getKitCooldown(String kitName){
        return this.kitCooldownMap.getOrDefault(kitName, Instant.ofEpochMilli(0));
    }


}

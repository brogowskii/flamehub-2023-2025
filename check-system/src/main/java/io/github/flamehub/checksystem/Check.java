package io.github.flamehub.checksystem;

import java.util.UUID;

public class Check {

    private final UUID id = UUID.randomUUID();

    private final UUID player;
    private final UUID admin;

    public Check(UUID player, UUID admin) {
        this.player = player;
        this.admin = admin;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPlayer() {
        return player;
    }

    public UUID getAdmin() {
        return admin;
    }
}

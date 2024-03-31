package io.github.flamehub.commons.user;

import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;

import java.util.UUID;

public class User {

    @Id
    private UUID uniqueId;

    @Indexed
    private String name;

    public User() {
    }

    public User(final UUID uniqueId, final String name) {
        this.uniqueId = uniqueId;
        this.name = name;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

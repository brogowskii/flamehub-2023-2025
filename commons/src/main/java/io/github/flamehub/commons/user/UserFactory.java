package io.github.flamehub.commons.user;

import java.util.UUID;
import java.util.function.BiFunction;

public class UserFactory<U extends User> {

    private final BiFunction<UUID, String, U> biFunction;

    public UserFactory(final BiFunction<UUID, String, U> biFunction) {
        this.biFunction = biFunction;
    }

    public U create(final UUID uuid, final String name) {
        return this.biFunction.apply(uuid, name);
    }
}

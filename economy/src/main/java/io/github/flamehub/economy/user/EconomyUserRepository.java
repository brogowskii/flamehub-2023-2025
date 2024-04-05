package io.github.flamehub.economy.user;

import dev.morphia.Datastore;
import io.github.flamehub.commons.user.UserDatabaseRepository;

final class EconomyUserRepository extends UserDatabaseRepository<EconomyUser> {
    EconomyUserRepository(final Datastore datastore) {
        super(datastore, EconomyUser.class);
    }
}

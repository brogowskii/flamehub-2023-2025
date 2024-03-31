package io.github.flamehub.essentials.user;

import dev.morphia.Datastore;
import io.github.flamehub.commons.user.UserDatabaseRepository;

final class EssentialsUserRepository extends UserDatabaseRepository<EssentialsUser> {
    EssentialsUserRepository(final Datastore datastore) {
        super(datastore, EssentialsUser.class);
    }
}

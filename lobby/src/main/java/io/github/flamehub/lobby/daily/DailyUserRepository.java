package io.github.flamehub.lobby.daily;

import dev.morphia.Datastore;
import io.github.flamehub.commons.user.UserDatabaseRepository;

public final class DailyUserRepository extends UserDatabaseRepository<DailyUser> {
    public DailyUserRepository(Datastore datastore) {
        super(datastore, DailyUser.class);
    }
}

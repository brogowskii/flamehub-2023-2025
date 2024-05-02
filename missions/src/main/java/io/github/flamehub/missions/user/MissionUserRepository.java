package io.github.flamehub.missions.user;

import dev.morphia.Datastore;
import io.github.flamehub.commons.user.UserDatabaseRepository;

public final class MissionUserRepository extends UserDatabaseRepository<MissionUser> {
    public MissionUserRepository(Datastore datastore) {
        super(datastore, MissionUser.class);
    }
}

package io.github.flamehub.economy.user;

import dev.morphia.Datastore;
import io.github.flamehub.commons.user.UserDatabaseRepository;

public class EconomyUserRepository extends UserDatabaseRepository<EconomyUser> {
    public EconomyUserRepository(Datastore datastore, Class<EconomyUser> entityClass) {
        super(datastore, entityClass);
    }
}

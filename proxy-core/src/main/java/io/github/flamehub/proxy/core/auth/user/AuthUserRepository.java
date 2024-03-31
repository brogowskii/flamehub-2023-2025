package io.github.flamehub.proxy.core.auth.user;

import dev.morphia.Datastore;
import io.github.flamehub.commons.database.DatabaseRepository;

public final class AuthUserRepository extends DatabaseRepository<AuthUser> {
    public AuthUserRepository(Datastore datastore, Class<AuthUser> entityClass) {
        super(datastore, entityClass);
    }
}

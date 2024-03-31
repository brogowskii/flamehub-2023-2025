package io.github.flamehub.code;

import dev.morphia.Datastore;
import io.github.flamehub.commons.user.UserDatabaseRepository;

public final class CodeUserRepository extends UserDatabaseRepository<CodeUser> {

    public CodeUserRepository(Datastore datastore, Class<CodeUser> entityClass) {
        super(datastore, entityClass);
    }
}

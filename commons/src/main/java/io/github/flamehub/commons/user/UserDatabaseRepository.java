package io.github.flamehub.commons.user;

import dev.morphia.Datastore;
import io.github.flamehub.commons.database.DatabaseRepository;

public class UserDatabaseRepository<U extends User> extends DatabaseRepository<U> {

  public UserDatabaseRepository(final Datastore datastore, final Class<U> entityClass) {
    super(datastore, entityClass);
  }

}

package io.github.flamehub.commons.user;

import dev.morphia.Datastore;
import io.github.flamehub.commons.database.DatabaseRepository;

public class UserRepository<U extends User> extends DatabaseRepository<U> {

  public UserRepository(final Datastore datastore, final Class<U> entityClass) {
    super(datastore, entityClass);
  }

}

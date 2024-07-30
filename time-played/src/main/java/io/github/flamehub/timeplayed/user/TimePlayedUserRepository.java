package io.github.flamehub.timeplayed.user;

import dev.morphia.Datastore;
import io.github.flamehub.commons.user.UserDatabaseRepository;

public final class TimePlayedUserRepository extends UserDatabaseRepository<TimePlayedUser> {

  public TimePlayedUserRepository(Datastore datastore, Class<TimePlayedUser> entityClass) {
    super(datastore, entityClass);
  }
}

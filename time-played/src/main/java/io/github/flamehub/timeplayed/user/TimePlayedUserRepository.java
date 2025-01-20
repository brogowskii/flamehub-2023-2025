package io.github.flamehub.timeplayed.user;

import dev.morphia.Datastore;
import io.github.flamehub.commons.user.UserRepository;

public final class TimePlayedUserRepository extends UserRepository<TimePlayedUser> {

  public TimePlayedUserRepository(Datastore datastore, Class<TimePlayedUser> entityClass) {
    super(datastore, entityClass);
  }
}

package io.github.flamehub.lobby.daily;

import dev.morphia.Datastore;
import io.github.flamehub.commons.user.UserRepository;

public final class DailyUserRepository extends UserRepository<DailyUser> {

  public DailyUserRepository(final Datastore datastore) {
    super(datastore, DailyUser.class);
  }
}

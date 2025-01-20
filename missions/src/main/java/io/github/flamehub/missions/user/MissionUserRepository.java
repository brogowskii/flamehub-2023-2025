package io.github.flamehub.missions.user;

import dev.morphia.Datastore;
import io.github.flamehub.commons.user.UserRepository;

public final class MissionUserRepository extends UserRepository<MissionUser> {

  public MissionUserRepository(Datastore datastore) {
    super(datastore, MissionUser.class);
  }
}

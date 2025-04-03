package io.github.flamehub.contest.user;

import dev.morphia.Datastore;
import io.github.flamehub.commons.user.UserRepository;

final class ContestUserRepository extends UserRepository<ContestUser> {

  public ContestUserRepository(final Datastore datastore) {
    super(datastore, ContestUser.class);
  }
}

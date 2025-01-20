package io.github.flamehub.economy.user;

import dev.morphia.Datastore;
import io.github.flamehub.commons.user.UserRepository;

final class EconomyUserRepository extends UserRepository<EconomyUser> {

  EconomyUserRepository(final Datastore datastore) {
    super(datastore, EconomyUser.class);
  }
}

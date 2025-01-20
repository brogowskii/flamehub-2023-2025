package io.github.flamehub.essentials.user;

import dev.morphia.Datastore;
import io.github.flamehub.commons.user.UserRepository;

final class EssentialsUserRepository extends UserRepository<EssentialsUser> {

  EssentialsUserRepository(final Datastore datastore) {
    super(datastore, EssentialsUser.class);
  }
}

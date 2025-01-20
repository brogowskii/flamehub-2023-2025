package io.github.flamehub.code;

import dev.morphia.Datastore;
import io.github.flamehub.commons.user.UserRepository;

final class CodeUserRepository extends UserRepository<CodeUser> {

  CodeUserRepository(final Datastore datastore) {
    super(datastore, CodeUser.class);
  }
}

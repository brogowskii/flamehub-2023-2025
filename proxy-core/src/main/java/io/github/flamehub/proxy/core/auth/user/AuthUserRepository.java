package io.github.flamehub.proxy.core.auth.user;

import dev.morphia.Datastore;
import io.github.flamehub.commons.database.DatabaseRepository;
import io.github.flamehub.commons.user.UserRepository;

public final class AuthUserRepository extends UserRepository<AuthUser> {

  public AuthUserRepository(final Datastore datastore) {
    super(datastore, AuthUser.class);
  }
}

package io.github.flamehub.commons.bukkit.user.impl;

import dev.morphia.Datastore;
import io.github.flamehub.commons.user.UserRepository;

public final class CommonUserRepository extends UserRepository<CommonUser> {

  public CommonUserRepository(final Datastore datastore) {
    super(datastore, CommonUser.class);
  }
}

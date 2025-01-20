package io.github.flamehub.kits.user;

import dev.morphia.Datastore;
import io.github.flamehub.commons.user.UserRepository;

public final class KitUserRepository extends UserRepository<KitUser> {

  public KitUserRepository(Datastore datastore, Class<KitUser> entityClass) {
    super(datastore, entityClass);
  }
}
package io.github.flamehub.kits.user;

import dev.morphia.Datastore;
import io.github.flamehub.commons.user.UserDatabaseRepository;

public final class KitUserRepository extends UserDatabaseRepository<KitUser> {

  public KitUserRepository(Datastore datastore, Class<KitUser> entityClass) {
    super(datastore, entityClass);
  }
}
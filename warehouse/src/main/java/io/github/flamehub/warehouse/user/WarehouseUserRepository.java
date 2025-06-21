package io.github.flamehub.warehouse.user;

import dev.morphia.Datastore;
import io.github.flamehub.commons.user.UserRepository;

public final class WarehouseUserRepository extends UserRepository<WarehouseUser> {

  public WarehouseUserRepository(final Datastore datastore) {
    super(datastore, WarehouseUser.class);
  }
}

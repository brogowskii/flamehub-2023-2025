package io.github.flamehub.warehouse.user;

import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserRepository;

public final class WarehouseUserCache extends UserDatabaseCache<WarehouseUser> {

  public WarehouseUserCache(final UserRepository<WarehouseUser> warehouseUserUserRepository) {
    super(warehouseUserUserRepository);
  }
}

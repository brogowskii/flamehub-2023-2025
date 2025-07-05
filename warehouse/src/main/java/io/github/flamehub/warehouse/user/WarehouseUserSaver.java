package io.github.flamehub.warehouse.user;

import io.github.flamehub.commons.bukkit.user.UserSaver;
import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserRepository;

public final class WarehouseUserSaver extends UserSaver<WarehouseUser> {

  public WarehouseUserSaver(
      final UserRepository<WarehouseUser> userRepository,
      final UserDatabaseCache<WarehouseUser> userDatabaseCache
  ) {
    super(userRepository, userDatabaseCache);
  }
}

package io.github.flamehub.kits.user;

import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserDatabaseRepository;

public final class KitUserCache extends UserDatabaseCache<KitUser> {

  public KitUserCache(UserDatabaseRepository<KitUser> bukkitPlayerDatabaseRepository) {
    super(bukkitPlayerDatabaseRepository);
  }
}

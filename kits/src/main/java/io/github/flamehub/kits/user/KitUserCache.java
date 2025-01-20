package io.github.flamehub.kits.user;

import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserRepository;

public final class KitUserCache extends UserDatabaseCache<KitUser> {

  public KitUserCache(UserRepository<KitUser> bukkitPlayerDatabaseRepository) {
    super(bukkitPlayerDatabaseRepository);
  }
}

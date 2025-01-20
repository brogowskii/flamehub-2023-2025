package io.github.flamehub.economy.user;

import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserRepository;

final class EconomyUserCache extends UserDatabaseCache<EconomyUser> {

  EconomyUserCache(final UserRepository<EconomyUser> bukkitPlayerDatabaseRepository) {
    super(bukkitPlayerDatabaseRepository);
  }
}

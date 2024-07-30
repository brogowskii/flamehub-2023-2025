package io.github.flamehub.economy.user;

import io.github.flamehub.commons.bukkit.user.UserSaver;
import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserDatabaseRepository;

final class EconomyUserSaver extends UserSaver<EconomyUser> {

  EconomyUserSaver(
      final UserDatabaseRepository<EconomyUser> userDatabaseRepository,
      final UserDatabaseCache<EconomyUser> userDatabaseCache
  ) {
    super(userDatabaseRepository, userDatabaseCache);
  }
}

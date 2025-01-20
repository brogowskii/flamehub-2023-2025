package io.github.flamehub.economy.user;

import io.github.flamehub.commons.bukkit.user.UserSaver;
import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserRepository;

final class EconomyUserSaver extends UserSaver<EconomyUser> {

  EconomyUserSaver(
      final UserRepository<EconomyUser> userRepository,
      final UserDatabaseCache<EconomyUser> userDatabaseCache
  ) {
    super(userRepository, userDatabaseCache);
  }
}

package io.github.flamehub.commons.bukkit.user;

import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserRepository;
import io.github.flamehub.commons.user.UserUpdatable;

public class UserSaver<U extends UserUpdatable> implements Runnable {

  private final UserRepository<U> userRepository;
  private final UserDatabaseCache<U> userDatabaseCache;

  public UserSaver(
      final UserRepository<U> userRepository,
      final UserDatabaseCache<U> userDatabaseCache
  ) {
    this.userRepository = userRepository;
    this.userDatabaseCache = userDatabaseCache;
  }

  @Override
  public void run() {
    userRepository.saveMany(userDatabaseCache.values()
        .stream()
        .filter(UserUpdatable::isNeedUpdate)
        .peek(UserUpdatable::markUpdated)
        .toList());
  }
}

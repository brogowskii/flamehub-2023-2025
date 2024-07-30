package io.github.flamehub.commons.bukkit.user;

import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserDatabaseRepository;
import io.github.flamehub.commons.user.UserUpdatable;

public class UserSaver<U extends UserUpdatable> implements Runnable {

  private final UserDatabaseRepository<U> userDatabaseRepository;
  private final UserDatabaseCache<U> userDatabaseCache;

  public UserSaver(
      final UserDatabaseRepository<U> userDatabaseRepository,
      final UserDatabaseCache<U> userDatabaseCache
  ) {
    this.userDatabaseRepository = userDatabaseRepository;
    this.userDatabaseCache = userDatabaseCache;
  }

  @Override
  public void run() {
    this.userDatabaseRepository.saveMany(this.userDatabaseCache.values()
        .stream()
        .filter(UserUpdatable::isNeedUpdate)
        .peek(UserUpdatable::markUpdated)
        .toList());
  }
}

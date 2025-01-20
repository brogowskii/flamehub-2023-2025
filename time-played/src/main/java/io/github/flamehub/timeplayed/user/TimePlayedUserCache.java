package io.github.flamehub.timeplayed.user;

import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserRepository;

public final class TimePlayedUserCache extends UserDatabaseCache<TimePlayedUser> {

  public TimePlayedUserCache(
      UserRepository<TimePlayedUser> bukkitPlayerDatabaseRepository) {
    super(bukkitPlayerDatabaseRepository);
  }
}

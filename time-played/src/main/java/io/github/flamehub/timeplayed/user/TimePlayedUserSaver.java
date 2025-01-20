package io.github.flamehub.timeplayed.user;

import io.github.flamehub.commons.bukkit.user.UserSaver;
import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserRepository;

public final class TimePlayedUserSaver extends UserSaver<TimePlayedUser> {

  public TimePlayedUserSaver(UserRepository<TimePlayedUser> userRepository,
      UserDatabaseCache<TimePlayedUser> userDatabaseCache) {
    super(userRepository, userDatabaseCache);
  }
}

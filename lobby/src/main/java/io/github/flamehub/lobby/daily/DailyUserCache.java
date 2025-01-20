package io.github.flamehub.lobby.daily;

import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserRepository;

public final class DailyUserCache extends UserDatabaseCache<DailyUser> {

  public DailyUserCache(UserRepository<DailyUser> bukkitPlayerDatabaseRepository) {
    super(bukkitPlayerDatabaseRepository);
  }
}

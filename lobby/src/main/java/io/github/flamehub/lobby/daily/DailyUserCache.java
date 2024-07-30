package io.github.flamehub.lobby.daily;

import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserDatabaseRepository;

public final class DailyUserCache extends UserDatabaseCache<DailyUser> {

  public DailyUserCache(UserDatabaseRepository<DailyUser> bukkitPlayerDatabaseRepository) {
    super(bukkitPlayerDatabaseRepository);
  }
}

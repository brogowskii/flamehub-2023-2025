package io.github.flamehub.daily.reward.user;

import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserDatabaseRepository;

public final class DailyRewardUserCache extends UserDatabaseCache<DailyRewardUser> {

  public DailyRewardUserCache(
      UserDatabaseRepository<DailyRewardUser> bukkitPlayerDatabaseRepository) {
    super(bukkitPlayerDatabaseRepository);
  }
}

package io.github.flamehub.daily.reward.user;

import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserRepository;

public final class DailyRewardUserCache extends UserDatabaseCache<DailyRewardUser> {

  public DailyRewardUserCache(
      UserRepository<DailyRewardUser> bukkitPlayerDatabaseRepository) {
    super(bukkitPlayerDatabaseRepository);
  }
}

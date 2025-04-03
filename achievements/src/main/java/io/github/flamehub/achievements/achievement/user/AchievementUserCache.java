package io.github.flamehub.achievements.achievement.user;

import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserRepository;

public final class AchievementUserCache extends UserDatabaseCache<AchievementUser> {

  public AchievementUserCache(final UserRepository<AchievementUser> bukkitPlayerDatabaseRepository) {
    super(bukkitPlayerDatabaseRepository);
  }
}

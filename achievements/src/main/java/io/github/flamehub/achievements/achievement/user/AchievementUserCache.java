package io.github.flamehub.achievements.achievement.user;

import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserDatabaseRepository;

public final class AchievementUserCache extends UserDatabaseCache<AchievementUser> {

  public AchievementUserCache(
      UserDatabaseRepository<AchievementUser> bukkitPlayerDatabaseRepository) {
    super(bukkitPlayerDatabaseRepository);
  }
}

package io.github.flamehub.achievements.achievement.user;

import io.github.flamehub.commons.bukkit.user.UserSaver;
import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserDatabaseRepository;

public final class AchievementUserSaver extends UserSaver<AchievementUser> {

  public AchievementUserSaver(UserDatabaseRepository<AchievementUser> userDatabaseRepository,
      UserDatabaseCache<AchievementUser> userDatabaseCache) {
    super(userDatabaseRepository, userDatabaseCache);
  }
}

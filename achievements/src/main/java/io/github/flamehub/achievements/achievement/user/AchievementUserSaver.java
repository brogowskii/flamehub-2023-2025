package io.github.flamehub.achievements.achievement.user;

import io.github.flamehub.commons.bukkit.user.UserSaver;
import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserRepository;

public final class AchievementUserSaver extends UserSaver<AchievementUser> {

  public AchievementUserSaver(UserRepository<AchievementUser> userRepository,
      UserDatabaseCache<AchievementUser> userDatabaseCache) {
    super(userRepository, userDatabaseCache);
  }
}

package io.github.flamehub.achievements.achievement.user;

import io.github.flamehub.commons.user.UserFactory;

public final class AchievementUserFactory extends UserFactory<AchievementUser> {

  public AchievementUserFactory() {
    super(AchievementUser::new);
  }
}

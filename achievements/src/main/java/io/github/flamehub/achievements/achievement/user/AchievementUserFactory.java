package io.github.flamehub.achievements.achievement.user;

import io.github.flamehub.commons.user.UserFactory;
import java.util.UUID;
import java.util.function.BiFunction;

public class AchievementUserFactory extends UserFactory<AchievementUser> {

  public AchievementUserFactory(BiFunction<UUID, String, AchievementUser> biFunction) {
    super(biFunction);
  }
}

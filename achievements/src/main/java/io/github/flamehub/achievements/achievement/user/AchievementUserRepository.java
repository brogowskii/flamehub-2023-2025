package io.github.flamehub.achievements.achievement.user;

import dev.morphia.Datastore;
import io.github.flamehub.commons.user.UserRepository;

public final class AchievementUserRepository extends UserRepository<AchievementUser> {

  public AchievementUserRepository(Datastore datastore) {
    super(datastore, AchievementUser.class);
  }
}

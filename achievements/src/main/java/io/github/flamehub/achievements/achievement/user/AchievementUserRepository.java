package io.github.flamehub.achievements.achievement.user;

import dev.morphia.Datastore;
import io.github.flamehub.commons.user.UserDatabaseRepository;

public class AchievementUserRepository extends UserDatabaseRepository<AchievementUser> {
    public AchievementUserRepository(Datastore datastore) {
        super(datastore, AchievementUser.class);
    }
}

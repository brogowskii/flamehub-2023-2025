package io.github.flamehub.daily.reward.user;

import dev.morphia.Datastore;
import io.github.flamehub.commons.user.UserDatabaseRepository;

public final class DailyRewardUserRepository extends UserDatabaseRepository<DailyRewardUser> {

  public DailyRewardUserRepository(Datastore datastore) {
    super(datastore, DailyRewardUser.class);
  }
}

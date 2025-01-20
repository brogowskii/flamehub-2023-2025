package io.github.flamehub.daily.reward.user;

import dev.morphia.Datastore;
import io.github.flamehub.commons.user.UserRepository;

public final class DailyRewardUserRepository extends UserRepository<DailyRewardUser> {

  public DailyRewardUserRepository(Datastore datastore) {
    super(datastore, DailyRewardUser.class);
  }
}

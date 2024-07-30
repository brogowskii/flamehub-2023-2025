package io.github.flamehub.daily.reward.user;

import io.github.flamehub.commons.user.UserFactory;

public final class DailyRewardUserFactory extends UserFactory<DailyRewardUser> {

  public DailyRewardUserFactory() {
    super(DailyRewardUser::new);
  }
}

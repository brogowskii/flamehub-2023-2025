package io.github.flamehub.contest.user;

import io.github.flamehub.commons.user.UserFactory;

final class ContestUserFactory extends UserFactory<ContestUser> {

  public ContestUserFactory() {
    super(ContestUser::new);
  }
}

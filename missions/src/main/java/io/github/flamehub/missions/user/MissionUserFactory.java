package io.github.flamehub.missions.user;

import io.github.flamehub.commons.user.UserFactory;

public final class MissionUserFactory extends UserFactory<MissionUser> {

  public MissionUserFactory() {
    super(MissionUser::new);
  }
}

package io.github.flamehub.missions.user;

import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserRepository;

public final class MissionUserCache extends UserDatabaseCache<MissionUser> {

  public MissionUserCache(UserRepository<MissionUser> missionUserUserRepository) {
    super(missionUserUserRepository);
  }
}

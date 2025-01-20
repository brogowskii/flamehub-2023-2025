package io.github.flamehub.missions.user;

import io.github.flamehub.commons.bukkit.user.UserSaver;
import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserRepository;

public final class MissionUserSaver extends UserSaver<MissionUser> {

  public MissionUserSaver(UserRepository<MissionUser> userRepository,
      UserDatabaseCache<MissionUser> userDatabaseCache) {
    super(userRepository, userDatabaseCache);
  }
}

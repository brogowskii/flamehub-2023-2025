package io.github.flamehub.contest.user;

import io.github.flamehub.commons.bukkit.user.UserSaver;
import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserRepository;

public final class ContestUserSaver extends UserSaver<ContestUser> {

  public ContestUserSaver(
      final UserRepository<ContestUser> userRepository,
      final UserDatabaseCache<ContestUser> userDatabaseCache) {
    super(userRepository, userDatabaseCache);
  }
}

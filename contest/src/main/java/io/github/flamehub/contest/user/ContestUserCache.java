package io.github.flamehub.contest.user;

import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserRepository;

final class ContestUserCache extends UserDatabaseCache<ContestUser> {

  public ContestUserCache(final UserRepository<ContestUser> contestUserUserRepository) {
    super(contestUserUserRepository);
  }
}

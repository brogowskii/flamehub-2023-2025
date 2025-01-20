package io.github.flamehub.essentials.user;

import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserRepository;

final class EssentialsUserCache extends UserDatabaseCache<EssentialsUser> {

  EssentialsUserCache(
      final UserRepository<EssentialsUser> essentialsUserUserRepository) {
    super(essentialsUserUserRepository);
  }
}

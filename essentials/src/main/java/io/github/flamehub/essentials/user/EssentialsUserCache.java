package io.github.flamehub.essentials.user;

import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserDatabaseRepository;

final class EssentialsUserCache extends UserDatabaseCache<EssentialsUser> {

  EssentialsUserCache(
      final UserDatabaseRepository<EssentialsUser> essentialsUserUserDatabaseRepository) {
    super(essentialsUserUserDatabaseRepository);
  }
}

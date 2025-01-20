package io.github.flamehub.code;

import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserRepository;

final class CodeUserCache extends UserDatabaseCache<CodeUser> {

  CodeUserCache(final UserRepository<CodeUser> userRepository) {
    super(userRepository);
  }
}

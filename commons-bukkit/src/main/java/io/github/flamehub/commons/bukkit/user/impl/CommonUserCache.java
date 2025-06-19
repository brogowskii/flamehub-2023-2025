package io.github.flamehub.commons.bukkit.user.impl;

import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserRepository;

public final class CommonUserCache extends UserDatabaseCache<CommonUser> {

  public CommonUserCache(final UserRepository<CommonUser> commonUserUserRepository) {
    super(commonUserUserRepository);
  }
}

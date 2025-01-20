package io.github.flamehub.tiktok.user;

import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserRepository;

public final class TikTokUserCache extends UserDatabaseCache<TikTokUser> {

  public TikTokUserCache(
      final UserRepository<TikTokUser> tikTokUserUserRepository) {
    super(tikTokUserUserRepository);
  }
}

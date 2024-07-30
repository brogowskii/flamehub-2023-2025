package io.github.flamehub.tiktok.user;

import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserDatabaseRepository;

final class TikTokUserCache extends UserDatabaseCache<TikTokUser> {

  public TikTokUserCache(
      final UserDatabaseRepository<TikTokUser> tikTokUserUserDatabaseRepository) {
    super(tikTokUserUserDatabaseRepository);
  }
}

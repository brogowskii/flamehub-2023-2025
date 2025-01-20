package io.github.flamehub.tiktok.user;

import io.github.flamehub.commons.bukkit.user.UserSaver;
import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserRepository;

public final class TikTokUserSaver extends UserSaver<TikTokUser> {

  public TikTokUserSaver(
      final UserRepository<TikTokUser> userRepository,
      final UserDatabaseCache<TikTokUser> userDatabaseCache) {
    super(userRepository, userDatabaseCache);
  }
}

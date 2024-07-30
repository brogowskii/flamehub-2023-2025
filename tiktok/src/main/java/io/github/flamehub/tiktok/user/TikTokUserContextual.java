package io.github.flamehub.tiktok.user;

import io.github.flamehub.commons.bukkit.user.UserContextual;
import io.github.flamehub.commons.user.UserDatabaseCache;

final class TikTokUserContextual extends UserContextual<TikTokUser> {

  public TikTokUserContextual(
      final UserDatabaseCache<TikTokUser> userCache) {
    super(userCache);
  }
}

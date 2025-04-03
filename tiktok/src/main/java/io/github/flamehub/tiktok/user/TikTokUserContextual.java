package io.github.flamehub.tiktok.user;

import io.github.flamehub.commons.bukkit.user.UserContextual;
import io.github.flamehub.commons.user.UserCache;
import io.github.flamehub.commons.user.UserDatabaseCache;

public final class TikTokUserContextual extends UserContextual<TikTokUser> {

  public TikTokUserContextual(final UserCache<TikTokUser> userCache) {
    super(userCache);
  }
}

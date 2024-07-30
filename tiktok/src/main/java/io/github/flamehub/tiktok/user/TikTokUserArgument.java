package io.github.flamehub.tiktok.user;

import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.user.UserArgument;
import io.github.flamehub.commons.user.UserDatabaseCache;

final class TikTokUserArgument extends UserArgument<TikTokUser> {

  public TikTokUserArgument(final UserDatabaseCache<TikTokUser> userCache,
      final BukkitMessagesService messagesService) {
    super(userCache, messagesService);
  }
}

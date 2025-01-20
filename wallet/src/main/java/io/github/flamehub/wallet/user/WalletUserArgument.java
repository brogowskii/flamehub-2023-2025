package io.github.flamehub.wallet.user;

import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.user.UserArgument;
import io.github.flamehub.commons.user.UserCache;
import io.github.flamehub.commons.user.UserDatabaseCache;

final class WalletUserArgument extends UserArgument<WalletUser> {

  public WalletUserArgument(UserCache<WalletUser> userCache,
      BukkitMessagesService messagesService) {
    super(userCache, messagesService);
  }
}

package io.github.flamehub.wallet.user;

import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.user.UserArgument;
import io.github.flamehub.commons.user.UserCache;

final class WalletUserArgument extends UserArgument<WalletUser> {

  public WalletUserArgument(
      final UserCache<WalletUser> userCache,
      final BukkitMessagesService messagesService
  ) {
    super(userCache, messagesService);
  }
}

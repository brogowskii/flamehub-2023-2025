package io.github.flamehub.wallet.user;

import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.user.UserArgument;
import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.wallet.api.WalletUser;

public final class WalletUserArgument extends UserArgument<WalletUser> {

  public WalletUserArgument(UserDatabaseCache<WalletUser> userCache,
      BukkitMessagesService messagesService) {
    super(userCache, messagesService);
  }
}

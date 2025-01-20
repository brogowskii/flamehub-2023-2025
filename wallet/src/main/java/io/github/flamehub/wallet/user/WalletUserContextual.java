package io.github.flamehub.wallet.user;

import io.github.flamehub.commons.bukkit.user.UserContextual;
import io.github.flamehub.commons.user.UserCache;
import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserRedisCache;

final class WalletUserContextual extends UserContextual<WalletUser> {

  public WalletUserContextual(UserCache<WalletUser> userCache) {
    super(userCache);
  }
}

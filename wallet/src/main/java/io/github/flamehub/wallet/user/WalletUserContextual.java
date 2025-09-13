package io.github.flamehub.wallet.user;

import io.github.flamehub.commons.bukkit.user.UserContextual;
import io.github.flamehub.commons.user.UserCache;

final class WalletUserContextual extends UserContextual<WalletUser> {

  public WalletUserContextual(final UserCache<WalletUser> userCache) {
    super(userCache);
  }
}

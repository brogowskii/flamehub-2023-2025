package io.github.flamehub.wallet.user;

import io.github.flamehub.commons.bukkit.user.UserContextual;
import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.wallet.api.WalletUser;

public final class WalletUserContextual extends UserContextual<WalletUser> {

  public WalletUserContextual(UserDatabaseCache<WalletUser> userCache) {
    super(userCache);
  }
}

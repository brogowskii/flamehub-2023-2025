package io.github.flamehub.wallet.user;

import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserDatabaseRepository;
import io.github.flamehub.wallet.api.WalletUser;

public final class WalletUserCache extends UserDatabaseCache<WalletUser> {

  public WalletUserCache(UserDatabaseRepository<WalletUser> bukkitPlayerDatabaseRepository) {
    super(bukkitPlayerDatabaseRepository);
  }
}

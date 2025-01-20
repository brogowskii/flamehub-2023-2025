package io.github.flamehub.wallet.user;

import io.github.flamehub.commons.util.ThrowingConsumer;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class WalletUserFacade {

  private final WalletUserCache walletUserCache;

  public WalletUserFacade(final WalletUserCache walletUserCache) {
    this.walletUserCache = walletUserCache;
  }

  public CompletableFuture<WalletUser> mutate(
      final UUID uuid,
      final ThrowingConsumer<WalletUser, Exception> mutator) {
    return walletUserCache.mutate(uuid, mutator);
  }

  public WalletUser findByUniqueId(final UUID uniqueId) {
    return walletUserCache.findByUniqueId(uniqueId);
  }

  public WalletUser findByName(final String name) {
    return walletUserCache.findByName(name);
  }



}

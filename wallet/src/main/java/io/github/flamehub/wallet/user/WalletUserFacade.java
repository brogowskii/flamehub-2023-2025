package io.github.flamehub.wallet.user;

import io.github.flamehub.commons.util.ThrowingConsumer;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class WalletUserFacade {

  private final WalletUserCache walletUserCache;
  private final WalletUserRepository walletUserRepository;

  public WalletUserFacade(
      final WalletUserCache walletUserCache,
      final WalletUserRepository walletUserRepository) {
    this.walletUserCache = walletUserCache;
    this.walletUserRepository = walletUserRepository;
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

  public void remove(final WalletUser user) {
    walletUserCache.remove(user);
  }

  public void save(final WalletUser user) {
    walletUserRepository.save(user);
  }

  public Collection<WalletUser> values() {
    return walletUserCache.values();
  }



}

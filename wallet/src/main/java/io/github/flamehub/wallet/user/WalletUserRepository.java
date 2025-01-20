package io.github.flamehub.wallet.user;

import dev.morphia.Datastore;
import io.github.flamehub.commons.user.UserRepository;

final class WalletUserRepository extends UserRepository<WalletUser> {

  public WalletUserRepository(Datastore datastore) {
    super(datastore, WalletUser.class);
  }
}

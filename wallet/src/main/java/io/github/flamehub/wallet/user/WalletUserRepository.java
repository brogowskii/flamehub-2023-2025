package io.github.flamehub.wallet.user;

import dev.morphia.Datastore;
import io.github.flamehub.commons.user.UserRepository;
import java.util.List;

final class WalletUserRepository extends UserRepository<WalletUser> {

  public WalletUserRepository(final Datastore datastore) {
    super(datastore, WalletUser.class);
  }

  public void deleteAllByName(final String name) {
    final List<WalletUser> users = loadAllIgnoreCase("name", name);
    for (final WalletUser user : users) {
      delete(user);
    }
  }
}

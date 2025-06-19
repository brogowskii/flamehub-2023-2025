package io.github.flamehub.coinflip.user;

import dev.morphia.Datastore;
import io.github.flamehub.commons.user.UserRepository;

public final class CoinFlipUserRepository extends UserRepository<CoinFlipUser> {

  public CoinFlipUserRepository(final Datastore datastore) {
    super(datastore, CoinFlipUser.class );
  }
}

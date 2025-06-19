package io.github.flamehub.coinflip.user;

import io.github.flamehub.commons.user.UserFactory;

public final class CoinFlipUserFactory extends UserFactory<CoinFlipUser> {

  public CoinFlipUserFactory() {
    super(CoinFlipUser::new);
  }
}

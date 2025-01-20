package io.github.flamehub.wallet.user;

import io.github.flamehub.commons.user.UserFactory;

final class WalletUserFactory extends UserFactory<WalletUser> {

  public WalletUserFactory() {
    super(WalletUser::new);
  }
}

package io.github.flamehub.wallet.user;

import io.github.flamehub.commons.user.UserFactory;
import io.github.flamehub.wallet.api.WalletUser;

public final class WalletUserFactory extends UserFactory<WalletUser> {

  public WalletUserFactory() {
    super(WalletUser::new);
  }
}

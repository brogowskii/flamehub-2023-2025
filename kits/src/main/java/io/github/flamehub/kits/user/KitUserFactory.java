package io.github.flamehub.kits.user;

import io.github.flamehub.commons.user.UserFactory;

public final class KitUserFactory extends UserFactory<KitUser> {

  public KitUserFactory() {
    super(KitUser::new);
  }
}

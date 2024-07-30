package io.github.flamehub.essentials.user;

import io.github.flamehub.commons.user.UserFactory;

final class EssentialsUserFactory extends UserFactory<EssentialsUser> {

  EssentialsUserFactory() {
    super(EssentialsUser::new);
  }
}

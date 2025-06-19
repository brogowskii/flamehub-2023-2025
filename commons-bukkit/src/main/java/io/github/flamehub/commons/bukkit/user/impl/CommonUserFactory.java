package io.github.flamehub.commons.bukkit.user.impl;

import io.github.flamehub.commons.user.UserFactory;
import java.util.UUID;
import java.util.function.BiFunction;

public final class CommonUserFactory extends UserFactory<CommonUser> {

  public CommonUserFactory() {
    super(CommonUser::new);
  }
}

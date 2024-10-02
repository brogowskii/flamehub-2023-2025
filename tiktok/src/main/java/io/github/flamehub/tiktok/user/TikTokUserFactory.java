package io.github.flamehub.tiktok.user;

import io.github.flamehub.commons.user.UserFactory;
import java.util.UUID;
import java.util.function.BiFunction;

public final class TikTokUserFactory extends UserFactory<TikTokUser> {

  public TikTokUserFactory() {
    super(TikTokUser::new);
  }
}

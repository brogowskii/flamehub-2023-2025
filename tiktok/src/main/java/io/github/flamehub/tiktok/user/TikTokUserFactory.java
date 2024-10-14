package io.github.flamehub.tiktok.user;

import io.github.flamehub.commons.user.UserFactory;

public final class TikTokUserFactory extends UserFactory<TikTokUser> {

  public TikTokUserFactory() {
    super(TikTokUser::new);
  }
}

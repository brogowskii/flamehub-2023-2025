package io.github.flamehub.tiktok.user;

import dev.morphia.Datastore;
import io.github.flamehub.commons.user.UserDatabaseRepository;

final class TikTokUserRepository extends UserDatabaseRepository<TikTokUser> {

  public TikTokUserRepository(final Datastore datastore) {
    super(datastore, TikTokUser.class);
  }
}

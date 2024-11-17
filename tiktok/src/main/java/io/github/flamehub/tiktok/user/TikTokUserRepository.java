package io.github.flamehub.tiktok.user;

import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import io.github.flamehub.commons.user.UserDatabaseRepository;

public final class TikTokUserRepository extends UserDatabaseRepository<TikTokUser> {

  public TikTokUserRepository(final Datastore datastore) {
    super(datastore, TikTokUser.class);
  }

  public TikTokUser loadBySecUid(String secUid) {
    return this.datastore.find(TikTokUser.class)
        .filter(Filters.eq("secUid", secUid))
        .first();
  }

}

package io.github.flamehub.tiktok.user;

import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import io.github.flamehub.commons.user.UserRepository;

public final class TikTokUserRepository extends UserRepository<TikTokUser> {

  public TikTokUserRepository(final Datastore datastore) {
    super(datastore, TikTokUser.class);
  }

  public TikTokUser loadBySecUid(String secUid) {
    return datastore.find(TikTokUser.class)
        .filter(Filters.eq("secUid", secUid))
        .first();
  }

}

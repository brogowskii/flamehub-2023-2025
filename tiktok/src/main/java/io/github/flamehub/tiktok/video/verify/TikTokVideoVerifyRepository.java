package io.github.flamehub.tiktok.video.verify;

import dev.morphia.Datastore;
import io.github.flamehub.commons.database.DatabaseRepository;

public final class TikTokVideoVerifyRepository extends DatabaseRepository<TikTokVideoVerify> {

  public TikTokVideoVerifyRepository(final Datastore datastore) {
    super(datastore, TikTokVideoVerify.class);
  }
}

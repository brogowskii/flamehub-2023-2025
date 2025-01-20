package io.github.flamehub.proxy.core.blacklist;

import dev.morphia.Datastore;
import io.github.flamehub.commons.database.DatabaseRepository;

public final class BlacklistRepository extends DatabaseRepository<Blacklist> {

  public BlacklistRepository(final Datastore datastore) {
    super(datastore, Blacklist.class);
  }
}

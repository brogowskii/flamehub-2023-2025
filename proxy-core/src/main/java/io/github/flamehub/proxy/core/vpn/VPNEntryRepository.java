package io.github.flamehub.proxy.core.vpn;

import dev.morphia.Datastore;
import io.github.flamehub.commons.database.DatabaseRepository;

public final class VPNEntryRepository extends DatabaseRepository<VPNEntry> {

  public VPNEntryRepository(final Datastore datastore) {
    super(datastore, VPNEntry.class);
  }
}

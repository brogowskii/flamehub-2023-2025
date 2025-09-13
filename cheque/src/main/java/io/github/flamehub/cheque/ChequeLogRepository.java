package io.github.flamehub.cheque;

import dev.morphia.Datastore;
import io.github.flamehub.commons.database.DatabaseRepository;

public final class ChequeLogRepository extends DatabaseRepository<ChequeLog> {

  public ChequeLogRepository(final Datastore datastore) {
    super(datastore, ChequeLog.class);
  }
}

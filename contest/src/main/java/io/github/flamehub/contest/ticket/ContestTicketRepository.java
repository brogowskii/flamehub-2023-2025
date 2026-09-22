package io.github.flamehub.contest.ticket;

import dev.morphia.Datastore;
import io.github.flamehub.commons.database.DatabaseRepository;

public final class ContestTicketRepository extends DatabaseRepository<ContestTicket> {

  public ContestTicketRepository(final Datastore datastore) {
    super(datastore, ContestTicket.class);
  }

}

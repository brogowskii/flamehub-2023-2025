package io.github.flamehub.contest.ticket;

import dev.morphia.Datastore;

public final class ContestTicketConfigurator {

  public static ContestTicketFacade create(final Datastore datastore) {
    final ContestTicketRepository contestTicketRepository = new ContestTicketRepository(datastore);
    return new ContestTicketFacade(contestTicketRepository);
  }

}

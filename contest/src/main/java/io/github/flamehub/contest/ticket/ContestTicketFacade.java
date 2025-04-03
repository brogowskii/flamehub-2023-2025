package io.github.flamehub.contest.ticket;

import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class ContestTicketFacade {

  private final ContestTicketRepository contestTicketRepository;

  public ContestTicketFacade(final ContestTicketRepository contestTicketRepository) {
    this.contestTicketRepository = contestTicketRepository;
  }

  public CompletableFuture<Void> createTicket(
      final UUID executorUniqueId,
      final String executorName) {
    return runAsync(() -> {
      final ContestTicket contestTicket = new ContestTicket(executorUniqueId, executorName);
      contestTicketRepository.save(contestTicket);
    });
  }

  public CompletableFuture<Integer> getTicketsAmount(final UUID executorUniqueId) {
    return supplyAsync(() -> contestTicketRepository.loadAll("userId", executorUniqueId).size());
  }
}

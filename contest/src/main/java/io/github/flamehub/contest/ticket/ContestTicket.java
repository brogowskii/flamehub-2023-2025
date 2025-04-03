package io.github.flamehub.contest.ticket;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import java.util.Date;
import java.util.UUID;

@Entity("contest_tickets")
public final class ContestTicket {

  @Id
  private UUID id = UUID.randomUUID();
  private UUID userId;
  private String username;
  private Date creationDate = new Date();

  public ContestTicket() {
  }

  public ContestTicket(final UUID userId, final String username) {
    this.userId = userId;
    this.username = username;
  }

  public ContestTicket(final UUID id, final UUID userId, final String username, final Date creationDate) {
    this.id = id;
    this.userId = userId;
    this.username = username;
    this.creationDate = creationDate;
  }

  public UUID getId() {
    return id;
  }

  public UUID getUserId() {
    return userId;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public String getUsername() {
    return username;
  }
}

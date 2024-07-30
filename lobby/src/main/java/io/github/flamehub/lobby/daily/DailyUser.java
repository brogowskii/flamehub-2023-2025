package io.github.flamehub.lobby.daily;

import dev.morphia.annotations.Entity;
import io.github.flamehub.commons.user.User;
import java.time.Instant;
import java.util.UUID;

@Entity("daily_users")
public final class DailyUser extends User {

  private Instant nextReceive;

  public DailyUser() {
  }

  public DailyUser(UUID uniqueId, String name) {
    super(uniqueId, name);
  }

  public Instant getNextReceive() {
    return nextReceive;
  }

  public void setNextReceive(Instant nextReceive) {
    this.nextReceive = nextReceive;
  }
}

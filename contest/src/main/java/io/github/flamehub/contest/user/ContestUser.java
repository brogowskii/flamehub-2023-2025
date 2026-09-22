package io.github.flamehub.contest.user;

import dev.morphia.annotations.Entity;
import io.github.flamehub.commons.user.UserUpdatable;
import java.util.UUID;

@Entity("contest_users")
public final class ContestUser extends UserUpdatable {

  private double contestPoints;

  public ContestUser() {
  }

  public ContestUser(final UUID uniqueId, final String name) {
    super(uniqueId, name);
  }

  public double getContestPoints() {
    return contestPoints;
  }

  public void setContestPoints(final double contestPoints) {
    this.contestPoints = contestPoints;
  }

  public void addContestPoints(final double contestPoints) {
    this.contestPoints += contestPoints;
  }

  public void removeContestPoints(final double contestPoints) {
    this.contestPoints -= contestPoints;
  }
}

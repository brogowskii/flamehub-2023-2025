package io.github.flamehub.contest;

import io.github.flamehub.contest.user.ContestUser;
import io.github.flamehub.contest.user.ContestUserFacade;
import io.github.flamehub.contest.user.ContestUserUpdateType;
import java.util.UUID;

public final class ContestFacade {

  private final ContestUserFacade contestUserFacade;

  public ContestFacade(final ContestUserFacade contestUserFacade) {
    this.contestUserFacade = contestUserFacade;
  }

  public double balance(final UUID uniqueId) {
    final ContestUser contestUser = contestUserFacade.findByUniqueId(uniqueId);
    return contestUser.getContestPoints();
  }

  public void addPoints(final UUID uniqueId, final double points) {
    final ContestUser contestUser = contestUserFacade.findByUniqueId(uniqueId);
    contestUser.addContestPoints(points);
    contestUserFacade.update(contestUser, points, ContestUserUpdateType.ADD);
  }

  public void removePoints(final UUID uniqueId, final double points) {
    final ContestUser contestUser = contestUserFacade.findByUniqueId(uniqueId);
    contestUser.removeContestPoints(points);
    contestUserFacade.update(contestUser, points, ContestUserUpdateType.REMOVE);
  }

  public void setPoints(final UUID uniqueId, final double points) {
    final ContestUser contestUser = contestUserFacade.findByUniqueId(uniqueId);
    contestUser.setContestPoints(points);
    contestUserFacade.update(contestUser, points, ContestUserUpdateType.SET);
  }
}

package io.github.flamehub.contest.user;

import io.github.flamehub.commons.messenger.packet.PacketHandler;

public final class ContestUserUpdateHandler {

  private final ContestUserFacade contestUserFacade;

  public ContestUserUpdateHandler(final ContestUserFacade contestUserFacade) {
    this.contestUserFacade = contestUserFacade;
  }

  @PacketHandler
  public void handle(ContestUserUpdate update) {
    final ContestUser contestUser = contestUserFacade.findByUniqueId(update.getUniqueId());
    if (contestUser == null) {
      return;
    }

    switch (update.getType()) {
      case ADD -> contestUser.addContestPoints(update.getValue());
      case REMOVE -> contestUser.removeContestPoints(update.getValue());
      case SET -> contestUser.setContestPoints(update.getValue());
    }

    contestUserFacade.save(contestUser);
  }


}

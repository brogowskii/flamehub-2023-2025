package io.github.flamehub.contest.user;

import java.util.UUID;

public final class ContestUserFacade {

  private final ContestUserCache contestUserCache;
  private final ContestUserRepository contestUserRepository;
  private final ContestUserUpdater contestUserUpdater;

  public ContestUserFacade(
      final ContestUserCache contestUserCache,
      final ContestUserRepository contestUserRepository,
      final ContestUserUpdater contestUserUpdater
  ) {
    this.contestUserCache = contestUserCache;
    this.contestUserRepository = contestUserRepository;
    this.contestUserUpdater = contestUserUpdater;
  }

  public ContestUser findByUniqueId(final UUID uniqueId) {
    return contestUserCache.findByUniqueId(uniqueId);
  }

  public ContestUser findByKey(final UUID uniqueId) {
    return contestUserCache.findByKey(uniqueId);
  }

  public void save(final ContestUser contestUser) {
    contestUserRepository.save(contestUser);
  }

  public void update(final ContestUser value, final double money,
      final ContestUserUpdateType type) {
    contestUserUpdater.update(value, money, type);
  }
}

package io.github.flamehub.economy.user;

import java.util.UUID;

public class EconomyUserFacade {

  private final EconomyUserCache economyUserCache;
  private final EconomyUserRepository economyUserRepository;
  private final EconomyUserUpdater economyUserUpdater;
  private final EconomyUserSaver economyUserSaver;

  public EconomyUserFacade(
      final EconomyUserCache economyUserCache,
      final EconomyUserRepository economyUserRepository,
      final EconomyUserUpdater economyUserUpdater,
      final EconomyUserSaver economyUserSaver
  ) {
    this.economyUserCache = economyUserCache;
    this.economyUserRepository = economyUserRepository;
    this.economyUserUpdater = economyUserUpdater;
    this.economyUserSaver = economyUserSaver;
  }

  public EconomyUser findByUniqueId(final UUID uuid) {
    return economyUserCache.findByUniqueId(uuid);
  }

  public EconomyUser findByName(final String name) {
    return economyUserCache.findByName(name);
  }

  public EconomyUser save(final EconomyUser economyUser) {
    return economyUserRepository.save(economyUser);
  }

  public void update(final EconomyUser value, final double money,
      final EconomyUserUpdateType type) {
    economyUserUpdater.update(value, money, type);
  }

  public void runSaveAll() {
    economyUserSaver.run();
  }

}

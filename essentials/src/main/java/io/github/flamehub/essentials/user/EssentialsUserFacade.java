package io.github.flamehub.essentials.user;

import java.util.Collection;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public final class EssentialsUserFacade {

  private final EssentialsUserCache essentialsUserCache;
  private final EssentialsUserFactory essentialsUserFactory;
  private final EssentialsUserRepository essentialsUserRepository;

  public EssentialsUserFacade(
      final EssentialsUserCache essentialsUserCache,
      final EssentialsUserFactory essentialsUserFactory,
      final EssentialsUserRepository essentialsUserRepository
  ) {
    this.essentialsUserCache = essentialsUserCache;
    this.essentialsUserFactory = essentialsUserFactory;
    this.essentialsUserRepository = essentialsUserRepository;
  }

  public EssentialsUser findByUniqueId(@NotNull final UUID uniqueId) {
    return this.essentialsUserCache.findByUniqueId(uniqueId);
  }

  public EssentialsUser findByName(@NotNull final String name) {
    return this.essentialsUserCache.findByName(name);
  }

  public EssentialsUser save(@NotNull final EssentialsUser essentialsUser) {
    return this.essentialsUserRepository.save(essentialsUser);
  }

  public Collection<EssentialsUser> values() {
    return this.essentialsUserCache.values();
  }
}

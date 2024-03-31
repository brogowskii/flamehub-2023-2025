package io.github.flamehub.economy.user;

import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserDatabaseRepository;

public final class EconomyUserCache extends UserDatabaseCache<EconomyUser> {
    public EconomyUserCache(UserDatabaseRepository<EconomyUser> bukkitPlayerDatabaseRepository) {
        super(bukkitPlayerDatabaseRepository);
    }
}

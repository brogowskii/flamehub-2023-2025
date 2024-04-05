package io.github.flamehub.economy.user;

import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserDatabaseRepository;

final class EconomyUserCache extends UserDatabaseCache<EconomyUser> {
    EconomyUserCache(final UserDatabaseRepository<EconomyUser> bukkitPlayerDatabaseRepository) {
        super(bukkitPlayerDatabaseRepository);
    }
}

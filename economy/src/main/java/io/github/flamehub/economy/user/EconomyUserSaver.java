package io.github.flamehub.economy.user;

import io.github.flamehub.commons.bukkit.user.UserSaver;
import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserDatabaseRepository;

public final class EconomyUserSaver extends UserSaver<EconomyUser> {
    public EconomyUserSaver(UserDatabaseRepository<EconomyUser> userDatabaseRepository, UserDatabaseCache<EconomyUser> userDatabaseCache) {
        super(userDatabaseRepository, userDatabaseCache);
    }
}

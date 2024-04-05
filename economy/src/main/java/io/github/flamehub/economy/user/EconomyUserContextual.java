package io.github.flamehub.economy.user;

import io.github.flamehub.commons.bukkit.user.UserContextual;
import io.github.flamehub.commons.user.UserDatabaseCache;

final class EconomyUserContextual extends UserContextual<EconomyUser> {
    EconomyUserContextual(final UserDatabaseCache<EconomyUser> userCache) {
        super(userCache);
    }
}

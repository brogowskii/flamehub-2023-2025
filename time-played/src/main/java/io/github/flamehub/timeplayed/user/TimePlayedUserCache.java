package io.github.flamehub.timeplayed.user;

import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserDatabaseRepository;

public final class TimePlayedUserCache extends UserDatabaseCache<TimePlayedUser> {
    public TimePlayedUserCache(UserDatabaseRepository<TimePlayedUser> bukkitPlayerDatabaseRepository) {
        super(bukkitPlayerDatabaseRepository);
    }
}

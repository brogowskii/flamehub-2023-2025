package io.github.flamehub.timeplayed.user;

import io.github.flamehub.commons.bukkit.user.UserSaver;
import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserDatabaseRepository;

public final class TimePlayedUserSaver extends UserSaver<TimePlayedUser> {
    public TimePlayedUserSaver(UserDatabaseRepository<TimePlayedUser> userDatabaseRepository, UserDatabaseCache<TimePlayedUser> userDatabaseCache) {
        super(userDatabaseRepository, userDatabaseCache);
    }
}

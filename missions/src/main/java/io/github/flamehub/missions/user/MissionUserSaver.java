package io.github.flamehub.missions.user;

import io.github.flamehub.commons.bukkit.user.UserSaver;
import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserDatabaseRepository;

public final class MissionUserSaver extends UserSaver<MissionUser> {
    public MissionUserSaver(UserDatabaseRepository<MissionUser> userDatabaseRepository, UserDatabaseCache<MissionUser> userDatabaseCache) {
        super(userDatabaseRepository, userDatabaseCache);
    }
}

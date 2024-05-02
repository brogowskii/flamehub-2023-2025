package io.github.flamehub.missions.user;

import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserDatabaseRepository;

public final class MissionUserCache extends UserDatabaseCache<MissionUser> {
    public MissionUserCache(UserDatabaseRepository<MissionUser> missionUserUserDatabaseRepository) {
        super(missionUserUserDatabaseRepository);
    }
}

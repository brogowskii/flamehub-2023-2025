package io.github.flamehub.code;

import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserDatabaseRepository;

public final class CodeUserCache extends UserDatabaseCache<CodeUser> {
    public CodeUserCache(UserDatabaseRepository<CodeUser> bukkitPlayerDatabaseRepository) {
        super(bukkitPlayerDatabaseRepository);
    }
}

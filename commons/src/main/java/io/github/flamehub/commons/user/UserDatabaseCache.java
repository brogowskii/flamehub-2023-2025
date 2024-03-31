package io.github.flamehub.commons.user;

import java.util.UUID;

public class UserDatabaseCache<U extends User> extends UserCache<U> {

    private final UserDatabaseRepository<U> userDatabaseRepository;

    public UserDatabaseCache(final UserDatabaseRepository<U> uUserDatabaseRepository) {
        this.userDatabaseRepository = uUserDatabaseRepository;
    }

    @Override
    public U findByUniqueId(final UUID uniqueId) {
        U player = this.usersByUniqueId.get(uniqueId);
        if (player == null) {
            player = this.userDatabaseRepository.load(uniqueId);
        }

        return player;
    }

    @Override
    public U findByName(final String name) {
        U player = this.usersByName.get(name.toLowerCase());
        if (player == null) {
            player = this.userDatabaseRepository.loadIgnoreCase("name", name);
        }

        return player;
    }

    public U findByKey(final UUID key) {
        return this.usersByUniqueId.get(key);
    }

}
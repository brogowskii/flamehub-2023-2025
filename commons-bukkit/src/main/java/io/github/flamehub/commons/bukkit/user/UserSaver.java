package io.github.flamehub.commons.bukkit.user;

import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserDatabaseRepository;
import io.github.flamehub.commons.user.UserUpdatable;

import java.util.List;

public class UserSaver<U extends UserUpdatable> implements Runnable {

    private final UserDatabaseRepository<U> userDatabaseRepository;
    private final UserDatabaseCache<U> userDatabaseCache;

    public UserSaver(UserDatabaseRepository<U> userDatabaseRepository, UserDatabaseCache<U> userDatabaseCache) {
        this.userDatabaseRepository = userDatabaseRepository;
        this.userDatabaseCache = userDatabaseCache;
    }

    @Override
    public void run() {
        List<U> usersToSave = userDatabaseCache.values()
                .stream()
                .filter(UserUpdatable::isNeedUpdate)
                .peek(user -> user.setNeedUpdate(false))
                .toList();

        int usersSaved = usersToSave.size();
        this.userDatabaseRepository.saveMany(usersToSave);

        System.out.println(getClass().getSimpleName() + " >> Zapisano " + usersSaved + " użytkowników potrzebujących zapisu!");
    }
}

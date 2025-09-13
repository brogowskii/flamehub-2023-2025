package io.github.flamehub.commons.user;

import java.util.UUID;

public class UserDatabaseCache<U extends User> extends UserLocallyCache<U> {

  private final UserRepository<U> userRepository;

  public UserDatabaseCache(final UserRepository<U> uUserRepository) {
    userRepository = uUserRepository;
  }

  @Override
  public U findByUniqueId(final UUID uniqueId) {
    U player = usersByUniqueId.get(uniqueId);
    if (player == null) {
      player = userRepository.load(uniqueId);
    }

    return player;
  }

  @Override
  public U findByName(final String name) {
    U player = usersByName.get(name.toLowerCase());
    if (player == null) {
      player = userRepository.loadIgnoreCase("name", name);
    }

    return player;
  }

  public U findByKey(final UUID key) {
    return usersByUniqueId.get(key);
  }

}
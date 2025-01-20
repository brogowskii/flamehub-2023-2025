package io.github.flamehub.commons.user;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserLocallyCache<U extends User> implements UserCache<U> {

  protected final Map<UUID, U> usersByUniqueId = new ConcurrentHashMap<>();
  protected final Map<String, U> usersByName = new ConcurrentHashMap<>();

  public U add(final U user) {
    usersByUniqueId.put(user.getUniqueId(), user);
    usersByName.put(user.getName().toLowerCase(), user);
    return user;
  }

  public void remove(final U user) {
    usersByUniqueId.remove(user.getUniqueId());
    usersByName.remove(user.getName().toLowerCase());
  }

  public void updateName(final U user, String newName) {
    usersByName.remove(user.getName());
    usersByName.put(newName.toLowerCase(), user);

    user.setName(newName);
  }

  @Override
  public U findByUniqueId(final UUID uuid) {
    return usersByUniqueId.get(uuid);
  }

  @Override
  public U findByName(final String name) {
    return usersByName.get(name.toLowerCase());
  }

  @Override
  public Collection<U> values() {
    return Collections.unmodifiableCollection(usersByUniqueId.values());
  }

}

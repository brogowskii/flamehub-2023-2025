package io.github.flamehub.commons.user;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserCache<U extends User> {

  protected final Map<UUID, U> usersByUniqueId = new ConcurrentHashMap<>();
  protected final Map<String, U> usersByName = new ConcurrentHashMap<>();

  public U add(final U user) {
    this.usersByUniqueId.put(user.getUniqueId(), user);
    this.usersByName.put(user.getName().toLowerCase(), user);
    return user;
  }

  public void remove(final U user) {
    this.usersByUniqueId.remove(user.getUniqueId());
    this.usersByName.remove(user.getName().toLowerCase());
  }

  public void updateName(final U user, String newName) {
    this.usersByName.remove(user.getName());
    this.usersByName.put(newName.toLowerCase(), user);

    user.setName(newName);
  }

  public U findByUniqueId(final UUID uuid) {
    return this.usersByUniqueId.get(uuid);
  }

  public U findByName(final String name) {
    return this.usersByName.get(name.toLowerCase());
  }

  public Collection<U> values() {
    return Collections.unmodifiableCollection(this.usersByUniqueId.values());
  }

}

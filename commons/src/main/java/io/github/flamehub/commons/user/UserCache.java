package io.github.flamehub.commons.user;

import java.util.Collection;
import java.util.UUID;

public interface UserCache<U extends User> {

  U findByUniqueId(final UUID uniqueId);

  U findByName(final String name);

  Collection<U> values();

}

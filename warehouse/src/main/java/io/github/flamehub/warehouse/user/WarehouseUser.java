package io.github.flamehub.warehouse.user;

import io.github.flamehub.commons.user.User;
import java.util.UUID;

public final class WarehouseUser extends User {

  public WarehouseUser() {
  }

  public WarehouseUser(final UUID uniqueId, final String name) {
    super(uniqueId, name);
  }
}

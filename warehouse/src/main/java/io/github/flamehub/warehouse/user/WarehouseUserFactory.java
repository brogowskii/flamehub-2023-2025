package io.github.flamehub.warehouse.user;

import io.github.flamehub.commons.user.UserFactory;

public final class WarehouseUserFactory extends UserFactory<WarehouseUser> {

  public WarehouseUserFactory() {
    super(WarehouseUser::new);
  }
}

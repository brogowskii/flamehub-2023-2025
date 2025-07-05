package io.github.flamehub.warehouse.user;

import dev.morphia.annotations.Entity;
import io.github.flamehub.commons.user.UserUpdatable;
import io.github.flamehub.warehouse.Warehouse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity("warehouse_users")
public final class WarehouseUser extends UserUpdatable {

  private Map<String, Warehouse> warehouseMap;

  public WarehouseUser() {
  }

  public WarehouseUser(final UUID uniqueId, final String name) {
    super(uniqueId, name);
  }

  public Map<String, Warehouse> getWarehouseMap() {
    if (warehouseMap == null) {
      warehouseMap = new HashMap<>();
    }

    return warehouseMap;
  }
}

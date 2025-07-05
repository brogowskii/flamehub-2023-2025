package io.github.flamehub.warehouse;

import java.util.UUID;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public final class WarehouseHolder implements InventoryHolder {

  private final String id;
  private final UUID owner;

  public WarehouseHolder(final String id, final UUID owner) {
    this.id = id;
    this.owner = owner;
  }

  @Override
  public @NotNull Inventory getInventory() {
    return null;
  }

  public String getId() {
    return id;
  }

  public UUID getOwner() {
    return owner;
  }
}

package io.github.flamehub.warehouse;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Transient;
import io.github.flamehub.commons.bukkit.util.SerializationUtil;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Entity
public final class Warehouse {

  private String id;

  @Transient
  private transient Inventory inventory;
  private String serializedContents;

  public Warehouse() {
  }

  public Warehouse(final String id) {
    this.id = id;
    serializedContents = SerializationUtil.serializeBukkitObject(new ItemStack[54]);
  }

  public String getId() {
    return id;
  }

  public String getSerializedContents() {
    return serializedContents;
  }

  public void setSerializedContents(final String serializedContents) {
    this.serializedContents = serializedContents;
  }

  public Inventory getInventory() {
    return inventory;
  }

  public void setInventory(final Inventory inventory) {
    this.inventory = inventory;
  }
}
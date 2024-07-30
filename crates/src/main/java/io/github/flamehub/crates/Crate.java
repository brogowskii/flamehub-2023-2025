package io.github.flamehub.crates;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public final class Crate implements Serializable {

  private Map<Integer, CrateItem> itemsBySlot;

  private String id;

  private long enabledFrom;
  private Location location;

  private String guiName;
  private ItemStack key;

  public Crate(String id) {
    this.id = id;
    this.enabledFrom = System.currentTimeMillis();
    this.itemsBySlot = new HashMap<>();
  }

  public Crate() {
  }

  public Map<Integer, CrateItem> getItemsBySlot() {
    return itemsBySlot;
  }

  @JsonIgnore
  public Collection<CrateItem> getItems() {
    return this.itemsBySlot.values();
  }

  public boolean isEnabled() {
    return Instant.ofEpochMilli(enabledFrom).isBefore(Instant.now());
  }

  public Instant getEnabledFrom() {
    return Instant.ofEpochMilli(enabledFrom);
  }

  public void setEnabledFrom(Instant enabledFrom) {
    this.enabledFrom = enabledFrom.toEpochMilli();
  }

  public String getId() {
    return id;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public String getGuiName() {
    return guiName;
  }

  public void setGuiName(String guiName) {
    this.guiName = guiName;
  }

  public ItemStack getKey() {
    return key;
  }

  public void setKey(ItemStack key) {
    this.key = key;
  }
}

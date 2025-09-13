package io.github.flamehub.armor.upgrade;

import java.io.Serializable;
import java.util.Objects;

public final class ArmorUpgradeType implements Serializable {

  private String id;   // unikalne ID typu (np. "SWORD")
  private int slot;    // slot w GUI
  private String name; // wyświetlana nazwa

  public ArmorUpgradeType() {
  }

  public ArmorUpgradeType(final String id, final int slot, final String name) {
    this.id = id;
    this.slot = slot;
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public int getSlot() {
    return slot;
  }

  public void setSlot(final int slot) {
    this.slot = slot;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ArmorUpgradeType)) return false;
    ArmorUpgradeType that = (ArmorUpgradeType) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "ArmorUpgradeType{id='" + id + "', slot=" + slot + ", name='" + name + "'}";
  }
}
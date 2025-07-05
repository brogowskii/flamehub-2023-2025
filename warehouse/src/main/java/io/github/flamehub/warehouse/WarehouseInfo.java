package io.github.flamehub.warehouse;

import dev.morphia.annotations.Entity;
import java.util.List;
import org.bukkit.Material;

@Entity
public final class WarehouseInfo {

  private final String name;
  private final List<String> description;
  private final Material icon;

  public WarehouseInfo(final String name, final List<String> description, final Material icon) {
    this.name = name;
    this.description = description;
    this.icon = icon;
  }

  public String getName() {
    return name;
  }

  public List<String> getDescription() {
    return description;
  }

  public Material getIcon() {
    return icon;
  }
}

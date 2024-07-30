package io.github.flamehub.crates;

import io.github.flamehub.commons.config.EnableRemote;
import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Location;

@FlameConfigProperties(name = "crates.json")
@EnableRemote(collection = "configs")
public final class CratesConfig extends FlameConfig {

  private Set<Crate> crates = new HashSet<>();

  public void add(Crate crate) {
    this.crates.add(crate);
  }

  public void remove(Crate crate) {
    this.crates.remove(crate);
  }

  public Crate findByLocation(Location location) {
    for (Crate crate : this.crates) {

      if (crate.getLocation() == null) {
        continue;
      }

      if (crate.getLocation().equals(location)) {
        return crate;
      }

    }

    return null;
  }

  public Crate findById(String id) {
    for (Crate crate : this.crates) {
      if (crate.getId().equalsIgnoreCase(id)) {
        return crate;
      }
    }

    return null;
  }

  public CrateItem random(Crate crate) {
    List<CrateItem> crateItems = List.copyOf(crate.getItems());
    if (crateItems.isEmpty()) {
      return null;
    }

    double totalChances = crateItems.stream()
        .mapToDouble(CrateItem::getChance)
        .sum();
    double randomValue = Math.random() * totalChances;

    for (CrateItem crateItem : crateItems) {
      randomValue -= crateItem.getChance();
      if (randomValue <= 0) {
        return crateItem;
      }
    }

    return crateItems.get(crateItems.size() - 1);
  }

  public Set<Crate> getCrates() {
    return crates;
  }
}

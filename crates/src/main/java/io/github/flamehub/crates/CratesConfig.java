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

  private final Set<Crate> crates = new HashSet<>();

  public void add(final Crate crate) {
    crates.add(crate);
  }

  public void remove(final Crate crate) {
    crates.remove(crate);
  }

  public Crate findByLocation(final Location location) {
    for (final Crate crate : crates) {

      if (crate.getLocation().isEmpty()) {
        continue;
      }

      if (crate.getLocation().contains(location)) {
        return crate;
      }

    }

    return null;
  }

  public Crate findById(final String id) {
    for (final Crate crate : crates) {
      if (crate.getId().equalsIgnoreCase(id)) {
        return crate;
      }
    }

    return null;
  }

  public CrateItem random(final Crate crate) {
    final List<CrateItem> crateItems = List.copyOf(crate.getItems());
    if (crateItems.isEmpty()) {
      return null;
    }

    final double totalChances = crateItems.stream()
        .mapToDouble(CrateItem::getChance)
        .sum();
    double randomValue = Math.random() * totalChances;

    for (CrateItem crateItem : crateItems) {
      randomValue -= crateItem.getChance();
      if (randomValue <= 0) {
        return crateItem;
      }
    }

    return crateItems.getLast();
  }

  public Set<Crate> getCrates() {
    return crates;
  }
}

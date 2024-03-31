package io.github.flamehub.crates;

import eu.okaeri.configs.OkaeriConfig;
import org.bukkit.Location;
import io.github.flamehub.crates.crate.Crate;
import io.github.flamehub.crates.crate.CrateItem;

import java.util.*;

public class CratesConfig extends OkaeriConfig {

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

    public Collection<Crate> getCrates() {
        return Collections.unmodifiableCollection(this.crates);
    }

}

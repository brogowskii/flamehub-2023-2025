package io.github.flamehub.essentials.warp;

import io.github.flamehub.commons.config.MongoConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

final class WarpConfig extends MongoConfig {

    private Map<String, Warp> warpMap = new HashMap<>(Map.of("skrzynki", new Warp("skrzynki", "&6&lSKRZYNIE PREMIUM", Material.CHEST, 23, new Location(Bukkit.getWorld("world"), 0,100,0))));

    WarpConfig() {
    }

    WarpConfig(final String id) {
        super(id);
    }

    Warp find(final String name) {
        return this.warpMap.get(name);
    }

    void add(final Warp warp) {
        this.warpMap.put(warp.getName(), warp);
    }

    void remove(Warp warp) {
        this.warpMap.remove(warp.getName());
    }

    Collection<Warp> values() {
        return this.warpMap.values();
    }
}

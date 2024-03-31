package io.github.flamehub.essentials.warp;

import io.github.flamehub.commons.config.MongoConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

final class WarpConfig extends MongoConfig {

    private Map<String, Warp> warpMap = new HashMap<>(Map.of("skrzynki", new Warp("&6&lSKRZYNIE PREMIUM", Material.CHEST, 23, new Location(Bukkit.getWorld("world"), 0,100,0))));

    WarpConfig() {
    }

    WarpConfig(final String id) {
        super(id);
    }

    public Warp findByName(final String name) {
        return this.warpMap.get(name);
    }

    public void putWarp(final String name, final Warp warp) {
        this.warpMap.put(name, warp);
    }

    public void removeWarp(final String name) {
        this.warpMap.remove(name);
    }

    public Map<String, Warp> getWarpMap() {
        return warpMap;
    }
}

package io.github.flamehub.essentials.warp;

import io.github.flamehub.commons.legacy.config.MongoConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

final class WarpConfig extends MongoConfig {

    private Map<String, Warp> warpMap = new HashMap<>(Map.of("skrzynki", new Warp("skrzynki", "&6&lSKRZYNIE PREMIUM", Material.CHEST, 23, new Location(Bukkit.getWorld("world"), 0,100,0))));

    WarpConfig() {
    }

    public Map<String, Warp> getWarpMap() {
        return warpMap;
    }

    WarpConfig(final String id) {
        super(id);
    }


}

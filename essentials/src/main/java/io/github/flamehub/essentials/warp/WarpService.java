package io.github.flamehub.essentials.warp;

import java.util.Collection;

final class WarpService {

    private final WarpConfig warpConfig;

    WarpService(WarpConfig warpConfig) {
        this.warpConfig = warpConfig;
    }

    Warp find(final String name) {
        return this.warpConfig.getWarpMap().get(name);
    }

    void add(final Warp warp) {
        this.warpConfig.getWarpMap().put(warp.getName(), warp);
    }

    void remove(Warp warp) {
        this.warpConfig.getWarpMap().remove(warp.getName());
    }

    Collection<Warp> values() {
        return this.warpConfig.getWarpMap().values();
    }

}

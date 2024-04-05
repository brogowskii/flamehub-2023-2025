package io.github.flamehub.essentials.warp;

import io.github.flamehub.commons.config.MongoConfigService;

import java.util.Collection;
import java.util.Collections;

public final class WarpFacade {

    private final WarpConfig warpConfig;

    WarpFacade(WarpConfig warpConfig) {
        this.warpConfig = warpConfig;
    }

    public Warp find(final String name) {
        return this.warpConfig.find(name);
    }

    void add(final Warp warp) {
        this.warpConfig.add(warp);
    }

    void remove(final Warp warp) {
        this.warpConfig.remove(warp);
    }

    public Collection<Warp> getWarps() {
        return Collections.unmodifiableCollection(this.warpConfig.values());
    }

    void saveConfig(MongoConfigService mongoConfigService) {
        mongoConfigService.save(this.warpConfig);
    }

    void refreshConfig(MongoConfigService mongoConfigService) throws IllegalAccessException {
        mongoConfigService.refresh(WarpConfig.class, this.warpConfig);
    }
    

}

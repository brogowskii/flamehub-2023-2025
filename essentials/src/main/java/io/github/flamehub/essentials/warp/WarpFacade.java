package io.github.flamehub.essentials.warp;

import io.github.flamehub.commons.config.MongoConfigService;

import java.util.Collection;
import java.util.Collections;

public final class WarpFacade {

    private final WarpConfig warpConfig;
    private final WarpService warpService;

    WarpFacade(WarpConfig warpConfig, WarpService warpService) {
        this.warpConfig = warpConfig;
        this.warpService = warpService;
    }

    public Warp find(final String name) {
        return this.warpService.find(name);
    }

    void add(final Warp warp) {
        this.warpService.add(warp);
    }

    void remove(final Warp warp) {
        this.warpService.remove(warp);
    }

    public Collection<Warp> getWarps() {
        return Collections.unmodifiableCollection(this.warpService.values());
    }

    void saveConfig(MongoConfigService mongoConfigService) {
        mongoConfigService.save(this.warpConfig);
    }

    void refreshConfig(MongoConfigService mongoConfigService) throws IllegalAccessException {
        mongoConfigService.refresh(WarpConfig.class, this.warpConfig);
    }
    

}

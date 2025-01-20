package io.github.flamehub.essentials.warp;

import io.github.flamehub.commons.config.FlameConfigService;
import java.util.Collection;
import java.util.Collections;

public final class WarpFacade {

  private final WarpService warpService;

  WarpFacade(WarpService warpService) {
    this.warpService = warpService;
  }

  public Warp find(final String name) {
    return warpService.find(name);
  }

  void add(final Warp warp) {
    warpService.add(warp);
  }

  void remove(final Warp warp) {
    warpService.remove(warp);
  }

  public Collection<Warp> getWarps() {
    return Collections.unmodifiableCollection(warpService.values());
  }

  void saveConfig(FlameConfigService flameConfigService) {
    flameConfigService.saveLocally(WarpConfig.class);
  }

  void refreshConfig(FlameConfigService flameConfigService) throws IllegalAccessException {
    flameConfigService.refreshLocally(WarpConfig.class);
  }


}

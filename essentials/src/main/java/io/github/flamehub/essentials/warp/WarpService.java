package io.github.flamehub.essentials.warp;

import java.util.Collection;

final class WarpService {

  private final WarpConfig warpConfig;

  WarpService(WarpConfig warpConfig) {
    this.warpConfig = warpConfig;
  }

  Warp find(final String name) {
    return warpConfig.getWarpMap().get(name);
  }

  void add(final Warp warp) {
    warpConfig.getWarpMap().put(warp.getName(), warp);
  }

  void remove(Warp warp) {
    warpConfig.getWarpMap().remove(warp.getName());
  }

  Collection<Warp> values() {
    return warpConfig.getWarpMap().values();
  }

}

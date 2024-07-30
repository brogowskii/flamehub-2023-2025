package io.github.flamehub.worldloader;

import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import java.util.ArrayList;
import java.util.List;

@FlameConfigProperties(name = "worldLoader.json")
public final class WorldLoaderConfig extends FlameConfig {

  private List<WorldLoader> worldLoaders = new ArrayList<>(
      List.of(new WorldLoader("pvp", "VoidGen")));

  public List<WorldLoader> getWorldLoaders() {
    return worldLoaders;
  }
}

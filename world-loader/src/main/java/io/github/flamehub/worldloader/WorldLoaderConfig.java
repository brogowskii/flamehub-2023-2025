package io.github.flamehub.worldloader;

import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.World.Environment;

@FlameConfigProperties(name = "worldLoader.json")
public final class WorldLoaderConfig extends FlameConfig {

  private final List<WorldLoader> worldLoaders = new ArrayList<>(
      List.of(new WorldLoader("pvp", "VoidGen", Environment.NORMAL)));

  public List<WorldLoader> getWorldLoaders() {
    return worldLoaders;
  }
}

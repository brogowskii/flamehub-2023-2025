package io.github.flamehub.worldloader;

import java.io.Serializable;
import org.bukkit.World;

public final class WorldLoader implements Serializable {

  private String name;
  private String generator;
  private World.Environment environment;

  private WorldLoader() {

  }

  public WorldLoader(
      final String name,
      final String generator,
      final World.Environment environment) {
    this.name = name;
    this.generator = generator;
    this.environment = environment;
  }

  public String getName() {
    return name;
  }

  public String getGenerator() {
    return generator;
  }

  public World.Environment getEnvironment() {
    return environment;
  }
}

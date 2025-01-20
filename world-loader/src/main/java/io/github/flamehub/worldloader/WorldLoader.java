package io.github.flamehub.worldloader;

import java.io.Serializable;
import org.bukkit.World.Environment;

public final class WorldLoader implements Serializable {

  private String name;
  private String generator;
  private Environment environment;

  private WorldLoader() {

  }

  public WorldLoader(
      final String name,
      final String generator,
      final Environment environment) {
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

  public Environment getEnvironment() {
    return environment;
  }
}

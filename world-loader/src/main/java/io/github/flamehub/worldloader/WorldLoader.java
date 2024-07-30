package io.github.flamehub.worldloader;

import java.io.Serializable;

public final class WorldLoader implements Serializable {

  private String name;
  private String generator;

  private WorldLoader() {

  }

  public WorldLoader(String name, String generator) {
    this.name = name;
    this.generator = generator;
  }

  public String getName() {
    return name;
  }

  public String getGenerator() {
    return generator;
  }
}

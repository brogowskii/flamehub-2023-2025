package io.github.flamehub.commons.database;

import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;

@FlameConfigProperties(name = "database.json")
public final class DatabaseConfig extends FlameConfig {

  private final String mongoUri = "example";

  public String getMongoUri() {
    return mongoUri;
  }
}

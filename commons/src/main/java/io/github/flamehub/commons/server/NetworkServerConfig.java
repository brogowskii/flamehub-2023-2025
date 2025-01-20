package io.github.flamehub.commons.server;

import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;

@FlameConfigProperties(name = "networkServer.json")
public final class NetworkServerConfig extends FlameConfig {

  private final String currentServerName = "xyz";
  private final String currentServerConfigsCollection = "configs";

  public String getCurrentServerName() {
    return currentServerName;
  }

  public String getCurrentServerConfigsCollection() {
    return currentServerConfigsCollection;
  }
}

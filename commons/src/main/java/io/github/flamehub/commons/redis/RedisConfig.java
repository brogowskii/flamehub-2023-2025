package io.github.flamehub.commons.redis;

import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;

@FlameConfigProperties(name = "redis.json")
public final class RedisConfig extends FlameConfig {

  private final String host = "localhost";
  private final int port = 6379;
  private final String password = "";

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getPassword() {
    return password;
  }
}

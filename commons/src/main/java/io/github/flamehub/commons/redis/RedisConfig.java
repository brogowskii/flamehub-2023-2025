package io.github.flamehub.commons.redis;

import eu.okaeri.configs.OkaeriConfig;
import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;

@FlameConfigProperties(name = "redis.json")
public final class RedisConfig extends FlameConfig {

    private String host = "localhost";
    private int port = 6379;
    private String password = "";

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

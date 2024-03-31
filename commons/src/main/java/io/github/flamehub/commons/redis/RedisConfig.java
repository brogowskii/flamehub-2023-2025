package io.github.flamehub.commons.redis;

import eu.okaeri.configs.OkaeriConfig;

public final class RedisConfig extends OkaeriConfig {

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

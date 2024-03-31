package io.github.flamehub.commons.database;

import eu.okaeri.configs.OkaeriConfig;

public final class DatabaseConfig extends OkaeriConfig {

    private String mongoUri = "example";

    public String getMongoUri() {
        return mongoUri;
    }
}

package io.github.flamehub.commons.server;

import eu.okaeri.configs.OkaeriConfig;

public final class NetworkServerConfig extends OkaeriConfig {

    private String currentServerName = "xyz";
    private String currentServerConfigsCollection = "configs";

    public String getCurrentServerName() {
        return currentServerName;
    }

    public String getCurrentServerConfigsCollection() {
        return currentServerConfigsCollection;
    }
}

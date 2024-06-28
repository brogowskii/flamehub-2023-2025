package io.github.flamehub.commons.config;

import java.io.File;

public class FlameConfig {

    private transient File dataFolder;

    public File getDataFolder() {
        return dataFolder;
    }

    public void setDataFolder(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    public FlameConfigProperties getProperties() {
        return this.getClass().getAnnotation(FlameConfigProperties.class);
    }

    public EnableRemote getRemote() {
        return this.getClass().getAnnotation(EnableRemote.class);
    }

}

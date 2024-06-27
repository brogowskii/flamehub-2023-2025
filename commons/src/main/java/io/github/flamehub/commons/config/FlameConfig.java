package io.github.flamehub.commons.config;

public class FlameConfig {

    public FlameConfigProperties getProperties() {
        return this.getClass().getAnnotation(FlameConfigProperties.class);
    }

    public EnableRemote getRemote() {
        return this.getClass().getAnnotation(EnableRemote.class);
    }

}

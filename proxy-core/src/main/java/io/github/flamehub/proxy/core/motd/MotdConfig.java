package io.github.flamehub.proxy.core.motd;

import eu.okaeri.configs.OkaeriConfig;

import java.util.Arrays;
import java.util.List;

public final class MotdConfig extends OkaeriConfig {

    private String first = "line1";
    private String second = "line2";

    private List<String> sample = Arrays.asList(
            "example"
    );

    public String getFormattedMotd() {
        StringBuilder formattedMotd = new StringBuilder();

        if (this.first != null && !this.first.isEmpty()) {
            formattedMotd.append(this.first).append('\n');
        }

        if (this.second != null && !this.second.isEmpty()) {
            formattedMotd.append(this.second);
        }

        return formattedMotd.toString();
    }

    public List<String> getSample() {
        return sample;
    }
}
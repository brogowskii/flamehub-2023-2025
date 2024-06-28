package io.github.flamehub.commons.bukkit.automessage;

import io.github.flamehub.commons.config.EnableRemote;
import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;

import java.util.ArrayList;
import java.util.List;

@FlameConfigProperties(name = "autoMessages.json")
public final class AutoMessageConfig extends FlameConfig {

    private List<AutoMessage> autoMessageList = new ArrayList<>();
    private int seconds = 30;

    public List<AutoMessage> getAutoMessageList() {
        return autoMessageList;
    }

    public int getSeconds() {
        return seconds;
    }
}

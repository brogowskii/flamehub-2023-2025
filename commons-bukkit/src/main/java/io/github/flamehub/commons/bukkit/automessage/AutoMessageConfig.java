package io.github.flamehub.commons.bukkit.automessage;

import eu.okaeri.configs.OkaeriConfig;

import java.util.ArrayList;
import java.util.List;

public final class AutoMessageConfig extends OkaeriConfig {

    private List<AutoMessage> autoMessageList = new ArrayList<>();
    private int seconds = 30;

    public List<AutoMessage> getAutoMessageList() {
        return autoMessageList;
    }

    public int getSeconds() {
        return seconds;
    }
}

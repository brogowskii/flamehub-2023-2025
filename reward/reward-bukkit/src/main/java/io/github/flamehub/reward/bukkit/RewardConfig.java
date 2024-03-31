package io.github.flamehub.reward.bukkit;

import eu.okaeri.configs.OkaeriConfig;

public class RewardConfig extends OkaeriConfig {

    private String command = "crate givekey epicka {PLAYER} 2";

    public String getCommand() {
        return command;
    }
}

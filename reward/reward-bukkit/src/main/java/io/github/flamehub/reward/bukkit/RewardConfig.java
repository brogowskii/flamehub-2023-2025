package io.github.flamehub.reward.bukkit;


import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;

@FlameConfigProperties(name = "reward.json")
public final class RewardConfig extends FlameConfig {

  private String command = "crate givekey epicka {PLAYER} 2";

  public String getCommand() {
    return command;
  }
}

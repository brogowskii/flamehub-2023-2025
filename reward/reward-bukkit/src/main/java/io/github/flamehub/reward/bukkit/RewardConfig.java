package io.github.flamehub.reward.bukkit;


import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import java.util.ArrayList;
import java.util.List;

@FlameConfigProperties(name = "reward.json")
public final class RewardConfig extends FlameConfig {

  private final List<String> command = new ArrayList<>(List.of("crate givekey epicka {PLAYER} 2"));

  public List<String> getCommand() {
    return command;
  }
}

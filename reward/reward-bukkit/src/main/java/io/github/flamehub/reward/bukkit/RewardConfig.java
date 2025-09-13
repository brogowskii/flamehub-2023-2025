package io.github.flamehub.reward.bukkit;


import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import java.util.ArrayList;
import java.util.List;

@FlameConfigProperties(name = "reward.json")
public final class RewardConfig extends FlameConfig {

  private List<String> command = new ArrayList<>(List.of("crate givekey epicka {PLAYER} 2", "crate givekey rzadka {PLAYER} 2"));
  private List<String> rewards = new ArrayList<>(List.of(
      "  &8▶ &f&lx2 &x&E&2&C&B&1&2&lᴋ&x&E&5&C&E&1&5&lʟ&x&E&8&D&1&1&9&lᴜ&x&E&B&D&4&1&C&lᴄ&x&E&E&D&7&1&F&lᴢ &x&F&4&D&D&2&6&lʟ&x&F&7&E&0&2&9&lᴇ&x&F&A&E&3&2&C&lɢ&x&F&7&E&0&2&8&lᴇ&x&F&3&D&C&2&5&lɴ&x&F&0&D&9&2&1&lᴅ&x&E&C&D&5&1&D&lᴀ&x&E&9&D&2&1&9&lʀ&x&E&5&C&E&1&6&lɴʏ",
      "  &8▶ &f&lx1 &x&4&6&C&A&3&E&lʀ&x&4&C&C&E&4&4&lᴢ&x&5&1&D&2&4&9&lᴀ&x&5&7&D&6&4&F&lᴅ&x&5&C&D&9&5&4&lᴋ&x&6&2&D&D&5&A&lɪ &x&6&0&D&C&5&8&lᴋ&x&5&A&D&8&5&2&lʟ&x&5&3&D&3&4&B&lᴜ&x&4&D&C&F&4&5&lᴄ&x&4&6&C&A&3&E&lᴢ"));

  public List<String> getCommand() {
    return command;
  }

  public List<String> getRewards() {
    return rewards;
  }
}

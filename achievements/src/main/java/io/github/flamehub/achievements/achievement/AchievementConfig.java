package io.github.flamehub.achievements.achievement;

import io.github.flamehub.commons.config.EnableRemote;
import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;

@FlameConfigProperties(name = "achievements.json")
@EnableRemote(collection = "configs")
public final class AchievementConfig extends FlameConfig {

  private Map<String, AchievementCategory> achievementCategories = Map.of(
      "mined_blocks", new AchievementCategory("mined_blocks",
          new AchievementAction(AchievementActionType.BLOCK_BREAK, null, null),
          "ᴡʏᴋᴏᴘᴀɴᴇ ʙʟᴏᴋɪ", Material.NETHERITE_PICKAXE, 20)
  );

  private Map<String, List<Achievement>> achievementsByCategory = Map.of(
      "mined_blocks", Arrays.asList(
          new Achievement(
              1,
              "mined_blocks",
              Arrays.asList(new AchievementReward("&bx1 Fragment Lodowca",
                  "upgradeadmin givecurrency {player} 1")),
              1000
          )
      )
  );

  public AchievementConfig() {
  }

  public Map<String, AchievementCategory> getAchievementCategories() {
    return achievementCategories;
  }

  public Map<String, List<Achievement>> getAchievementsByCategory() {
    return achievementsByCategory;
  }
}

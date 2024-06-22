package io.github.flamehub.achievements.achievement;

import io.github.flamehub.commons.config.MongoConfig;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class AchievementConfig extends MongoConfig {

    private Map<String, AchievementCategory> achievementCategories = Map.of(
            "mined_blocks", new AchievementCategory("mined_blocks", new AchievementAction(AchievementActionType.BLOCK_BREAK, null, null),
                    "ᴡʏᴋᴏᴘᴀɴᴇ ʙʟᴏᴋɪ", Material.NETHERITE_PICKAXE, 20)
    );

    private Map<String, List<Achievement>> achievementsByCategory = Map.of(
            "mined_blocks", Arrays.asList(
                    new Achievement(
                            1,
                            "mined_blocks",
                            Arrays.asList(new AchievementReward("&bx1 Fragment Lodowca", "upgradeadmin givecurrency {player} 1")),
                            1000
                    )
            )
    );

    public AchievementConfig() {
    }

    public AchievementConfig(String id) {
        super(id);
    }

    public Map<String, AchievementCategory> getAchievementCategories() {
        return achievementCategories;
    }

    public Map<String, List<Achievement>> getAchievementsByCategory() {
        return achievementsByCategory;
    }
}

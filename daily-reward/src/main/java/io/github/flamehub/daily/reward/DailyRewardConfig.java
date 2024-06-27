package io.github.flamehub.daily.reward;

import io.github.flamehub.commons.legacy.config.MongoConfig;

import java.util.Arrays;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public final class DailyRewardConfig extends MongoConfig {

    private SortedMap<Integer, DailyReward> dailyRewardMap = new TreeMap<>(Map.of(
            1,
            new DailyReward(
                    1,
                    Arrays.asList(
                            "upgradeadmin givecurrency {player} 1",
                            "crate givekey afk {player} 1"
                    ),
                    Arrays.asList(
                            "&bx1 Fragment Lodowca",
                            "&eKlucz do skrzyni AFK"
                    )
            ),
            2,
            new DailyReward(
                    2,
                    Arrays.asList(
                            "upgradeadmin givecurrency {player} 1",
                            "crate givekey afk {player} 1"
                    ),
                    Arrays.asList(
                            "&bx1 Fragment Lodowca",
                            "&eKlucz do skrzyni AFK"
                    )
            ),
            3,
            new DailyReward(
                    3,
                    Arrays.asList(
                            "upgradeadmin givecurrency {player} 1",
                            "crate givekey afk {player} 1"
                    ),
                    Arrays.asList(
                            "&bx1 Fragment Lodowca",
                            "&eKlucz do skrzyni AFK"
                    )
            )
    ));

    public DailyRewardConfig() {
    }

    public DailyRewardConfig(String id) {
        super(id);
    }

    public Map<Integer, DailyReward> getDailyRewardMap() {
        return dailyRewardMap;
    }
}

package io.github.flamehub.daily.reward;

import io.github.flamehub.commons.config.EnableRemote;
import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import java.util.Arrays;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@FlameConfigProperties(name = "dailyReward.json")
@EnableRemote(collection = "configs")
public final class DailyRewardConfig extends FlameConfig {

  private final SortedMap<Integer, DailyReward> dailyRewardMap = new TreeMap<>(Map.of(
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

  public Map<Integer, DailyReward> getDailyRewardMap() {
    return dailyRewardMap;
  }
}

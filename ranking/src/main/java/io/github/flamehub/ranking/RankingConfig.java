package io.github.flamehub.ranking;

import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import io.github.flamehub.ranking.gui.RankingGuiWrapper;
import io.github.flamehub.ranking.info.RankingInfo;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;

@FlameConfigProperties(name = "ranking.json")
public final class RankingConfig extends FlameConfig {

  private final RankingInfo playedTime = new RankingInfo(
      "spend-time",
      "player-tops",
      new RankingItem(
          "&6&lTOPKA SPĘDZONEGO CZASU",
          " &8#{POSITION}. &7{ENTRY} &8- &f{VALUE} &7(&espędzonego czasu&7)",
          List.of(""),
          24,
          Material.CLOCK
      ),
      "time_played_users",
      "boxpvp",
      List.of("spendTime"),
      "name",
      17
  );

  private final List<RankingGuiWrapper> rankingGuiList = List.of(
      new RankingGuiWrapper("player-tops", "&8&lTopki graczy")
  );

  private final List<RankingInfo> rankingInfoList = List.of(

      new RankingInfo(
          "money",
          "player-tops",
          new RankingItem(
              "&6&lTOPKA POSIADANYCH PIENIĘDZY",
              " &8#{POSITION}. &7{ENTRY} &8- &f{VALUE} &7(&eposiadanych pieniędzy&7)",
              List.of(""),
              13,
              Material.SUNFLOWER
          ),
          "economy_users",
          "boxpvp",
          List.of("money"),
          "name",
          17
      )
  );

  public RankingInfo getPlayedTime() {
    return playedTime;
  }

  public List<RankingGuiWrapper> getRankingGuiList() {
    return rankingGuiList;
  }

  public List<RankingInfo> getRankingInfoList() {
    return rankingInfoList;
  }
}

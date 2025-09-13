package io.github.flamehub.ranking.gui;

import java.util.HashMap;
import java.util.Map;

public final class RankingGuiCache {

  private final Map<String, RankingGui> rankingGuiMap = new HashMap<>();

  public void add(final RankingGui rankingGui) {
    rankingGuiMap.put(rankingGui.getRankingGuiWrapper().getId(), rankingGui);
  }

  public RankingGui findById(final String id) {
    return rankingGuiMap.get(id);
  }

}

package io.github.flamehub.ranking;

import io.github.flamehub.ranking.info.RankingInfo;
import java.util.ArrayList;
import java.util.List;

public final class RankingWrapper {

  private final List<RankingEntry> entries = new ArrayList<>();

  private final RankingInfo info;

  public RankingWrapper(RankingInfo info) {
    this.info = info;
  }

  public int getPlace(String entry) {
    for (int i = 0; i < entries.size(); ++i) {
      if (entries.get(i).getName().equals(entry)) {
        return i + 1;
      }
    }
    return 0;
  }

  public RankingInfo getInfo() {
    return info;
  }

  public List<RankingEntry> getEntries() {
    return entries;
  }

}

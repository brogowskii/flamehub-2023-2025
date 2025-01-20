package io.github.flamehub.ranking;

import io.github.flamehub.ranking.info.RankingInfo;
import io.github.flamehub.ranking.info.RankingInfoCache;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class RankingCache {

  private final Map<RankingInfo, RankingWrapper> infoRankingWrapperMap = new ConcurrentHashMap<>();

  private final RankingRepository rankingRepository;
  private final RankingInfoCache rankingInfoCache;

  public RankingCache(RankingRepository rankingRepository, RankingInfoCache rankingInfoCache) {
    this.rankingRepository = rankingRepository;
    this.rankingInfoCache = rankingInfoCache;
  }

  public void setup() {
    infoRankingWrapperMap.clear();
    rankingInfoCache.values()
        .forEach(rankingInfo -> infoRankingWrapperMap.put(rankingInfo,
            new RankingWrapper(rankingInfo)));
  }

  public RankingWrapper findByInfo(String info) {
    RankingInfo rankingInfo = rankingInfoCache.findById(info);
    return infoRankingWrapperMap.get(rankingInfo);
  }

  public void update(RankingInfo info) {

    List<RankingEntry> rankingEntries = rankingRepository.loadByInfo(info);
    List<RankingEntry> entries = findByInfo(info.getId()).getEntries();
    entries.clear();
    entries.addAll(rankingEntries);

  }

  public Collection<RankingWrapper> values() {
    return Collections.unmodifiableCollection(infoRankingWrapperMap.values());
  }


}

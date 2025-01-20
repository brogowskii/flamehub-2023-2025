package io.github.flamehub.ranking;

import io.github.flamehub.ranking.info.RankingInfoCache;

public final class RankingRefresher implements Runnable {

  private final RankingInfoCache rankingInfoCache;
  private final RankingCache rankingCache;

  public RankingRefresher(RankingInfoCache rankingInfoCache, RankingCache rankingCache) {
    this.rankingInfoCache = rankingInfoCache;
    this.rankingCache = rankingCache;
  }

  @Override
  public void run() {
    rankingInfoCache.values().forEach(rankingCache::update);
  }
}

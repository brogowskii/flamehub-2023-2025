package io.github.flamehub.ranking.info;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class RankingInfoCache {

    private final Map<String, RankingInfo> rankingTypeMap = new ConcurrentHashMap<>();

    public RankingInfo findById(String id) {
        return this.rankingTypeMap.get(id);
    }

    public void addType(RankingInfo rankingInfo) {
        this.rankingTypeMap.put(rankingInfo.getId(), rankingInfo);
    }

    public Collection<RankingInfo> values() {
        return Collections.unmodifiableCollection(this.rankingTypeMap.values());
    }

}

package io.github.flamehub.reward.api;

import dev.morphia.Datastore;
import dev.morphia.query.filters.Filter;
import dev.morphia.query.filters.Filters;
import dev.morphia.query.filters.LogicalFilter;
import dev.morphia.query.filters.RegexFilter;
import io.github.flamehub.commons.database.DatabaseRepository;

public final class RewardReceivedEntryRepository extends DatabaseRepository<RewardReceivedEntry> {

    public RewardReceivedEntryRepository(Datastore datastore, Class<RewardReceivedEntry> entityClass) {
        super(datastore, entityClass);
    }

    public RewardReceivedEntry loadByPlayerNameAndServerCategory(String playerName, String serverCategory) {
        RegexFilter playerNameFilter = Filters.regex("_id").pattern(playerName).caseInsensitive();
        Filter serverCategoryFilter = Filters.eq("serverCategory", serverCategory);
        LogicalFilter and = Filters.and(playerNameFilter, serverCategoryFilter);
        return this.datastore.find(RewardReceivedEntry.class)
                .filter(and)
                .first();
    }

    public RewardReceivedEntry loadByUserIdAndServerCategory(long userId, String serverCategory) {
        return this.datastore.find(RewardReceivedEntry.class)
                .filter(Filters.eq("userId", userId), Filters.eq("serverCategory", serverCategory))
                .first();
    }

}

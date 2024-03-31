package io.github.flamehub.checksystem.history;

import dev.morphia.Datastore;
import io.github.flamehub.commons.database.DatabaseRepository;

public final class CheckHistoryRepository extends DatabaseRepository<CheckHistory> {
    public CheckHistoryRepository(Datastore datastore) {
        super(datastore, CheckHistory.class);
    }
}

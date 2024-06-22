package io.github.flamehub.essentials.vanish;

import dev.morphia.Datastore;
import io.github.flamehub.commons.database.DatabaseRepository;

final class VanishedEntryRepository extends DatabaseRepository<VanishedEntry> {
    VanishedEntryRepository(final Datastore datastore) {
        super(datastore, VanishedEntry.class);
    }
}

package io.github.flamehub.player.sync.data;

import dev.morphia.Datastore;
import io.github.flamehub.commons.database.DatabaseRepository;

public final class PlayerSyncDataRepository extends DatabaseRepository<PlayerSyncData> {
    public PlayerSyncDataRepository(Datastore datastore) {
        super(datastore, PlayerSyncData.class);
    }
}

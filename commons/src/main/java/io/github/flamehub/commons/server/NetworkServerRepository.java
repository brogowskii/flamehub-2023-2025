package io.github.flamehub.commons.server;

import dev.morphia.Datastore;
import io.github.flamehub.commons.database.DatabaseRepository;

public final class NetworkServerRepository extends DatabaseRepository<NetworkServer> {
    public NetworkServerRepository(Datastore datastore, Class<NetworkServer> entityClass) {
        super(datastore, entityClass);
    }
}

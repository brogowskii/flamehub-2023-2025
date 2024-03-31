package io.github.flamehub.wallet.api;

import dev.morphia.Datastore;
import io.github.flamehub.commons.user.UserDatabaseRepository;

public final class WalletUserRepository extends UserDatabaseRepository<WalletUser> {

    public WalletUserRepository(Datastore datastore, Class<WalletUser> entityClass) {
        super(datastore, entityClass);
    }
}

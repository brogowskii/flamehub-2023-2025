package io.github.flamehub.essentials.user;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.user.UserDatabaseListener;
import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserDatabaseRepository;
import io.github.flamehub.commons.user.UserFactory;
import org.bukkit.plugin.PluginManager;

class EssentialsUserListener extends UserDatabaseListener<EssentialsUser> {
    public EssentialsUserListener(
            final FlameDispatcher flameDispatcher,
            final PluginManager pluginManager,
            final UserDatabaseCache<EssentialsUser> userDatabaseCache,
            final UserDatabaseRepository<EssentialsUser> userDatabaseRepository,
            final UserFactory<EssentialsUser> userFactory
    ) {
        super(flameDispatcher, pluginManager, userDatabaseCache, userDatabaseRepository, userFactory);
    }
}

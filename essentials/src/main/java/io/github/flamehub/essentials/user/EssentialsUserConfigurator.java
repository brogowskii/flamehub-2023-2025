package io.github.flamehub.essentials.user;

import com.mongodb.client.MongoClient;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import io.github.flamehub.commons.bukkit.BukkitConfigurator;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.database.DatastoreFactory;
import org.bukkit.plugin.Plugin;

public final class EssentialsUserConfigurator extends BukkitConfigurator {

    public EssentialsUserFacade essentialsUserFacade(
            final Plugin plugin,
            final FlameDispatcher flameDispatcher,
            final MongoClient mongoClient,
            final String databaseName
    ) {

        final EssentialsUserRepository essentialsUserRepository = new EssentialsUserRepository(DatastoreFactory.create(mongoClient, databaseName, EssentialsUser.class));
        final EssentialsUserCache essentialsUserCache = new EssentialsUserCache(essentialsUserRepository);
        final EssentialsUserFactory essentialsUserFactory = new EssentialsUserFactory();

        this.registerListeners(
                plugin,
                new EssentialsUserListener(
                        flameDispatcher,
                        plugin.getServer().getPluginManager(),
                        essentialsUserCache,
                        essentialsUserRepository,
                        essentialsUserFactory
                )
        );

        return new EssentialsUserFacade(essentialsUserCache, essentialsUserFactory, essentialsUserRepository);
    }

}

package io.github.flamehub.restapi.server;

import dev.morphia.Morphia;
import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.server.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.logging.Logger;

@Configuration
class NetworkServerConfiguration {

    Logger logger = Logger.getLogger("network-server-logger");

    @Bean
    NetworkServerCache networkServerCache(RedisMessenger redisMessenger) {

        NetworkServerCache networkServerCache = new NetworkServerCache();
        redisMessenger.subscribe("network_servers", new NetworkServerUpdateHandler(logger, networkServerCache));

        return networkServerCache;
    }

    @Bean
    NetworkServerRepository networkServerRepository(DatabaseConnector databaseConnector) {
        return new NetworkServerRepository(
                DatastoreFactory.create(databaseConnector.getMongoClient(), "global", NetworkServer.class),
                NetworkServer.class
        );
    }

    @Bean
    NetworkServerLoader networkServerLoader(NetworkServerCache networkServerCache, NetworkServerRepository networkServerRepository) {
        NetworkServerLoader networkServerLoader = new NetworkServerLoader(logger, networkServerCache, networkServerRepository, "");
        networkServerLoader.load();
        return networkServerLoader;
    }



}

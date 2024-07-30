package io.github.flamehub.restapi.server;

import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.commons.server.NetworkServerLoader;
import io.github.flamehub.commons.server.NetworkServerRepository;
import io.github.flamehub.commons.server.NetworkServerUpdateHandler;
import java.util.logging.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class NetworkServerConfiguration {

  Logger logger = Logger.getLogger("network-server-logger");

  @Bean
  NetworkServerCache networkServerCache(RedisMessenger redisMessenger) {

    NetworkServerCache networkServerCache = new NetworkServerCache();
    redisMessenger.subscribe("network_servers",
        new NetworkServerUpdateHandler(logger, networkServerCache));

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
  NetworkServerLoader networkServerLoader(NetworkServerCache networkServerCache,
      NetworkServerRepository networkServerRepository) {
    NetworkServerLoader networkServerLoader = new NetworkServerLoader(logger, networkServerCache,
        networkServerRepository, "");
    networkServerLoader.load();
    return networkServerLoader;
  }


}

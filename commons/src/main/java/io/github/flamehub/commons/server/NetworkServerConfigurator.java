package io.github.flamehub.commons.server;

import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.redis.RedisService;

public final class NetworkServerConfigurator {

  public NetworkServerFacade networkServerFacade(
      final RedisMessenger redisMessenger,
      final RedisService redisService,
      final NetworkServer currentServer
  ) {

    final NetworkServerSettingsCache networkServerSettingsCache = new NetworkServerSettingsCache(
        redisMessenger,
        redisService
    );

    final NetworkServerFacade networkServerFacade = new NetworkServerFacade(
        redisMessenger,
        redisService,
        networkServerSettingsCache
    );
    networkServerFacade.setCurrent(currentServer);
    return networkServerFacade;
  }

}

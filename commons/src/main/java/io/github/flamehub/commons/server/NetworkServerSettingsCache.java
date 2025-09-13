package io.github.flamehub.commons.server;

import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.redis.RedisService;
import io.github.flamehub.commons.redis.cache.RedisCache;

final class NetworkServerSettingsCache extends RedisCache<String, NetworkServerSettings> {

  public NetworkServerSettingsCache(
      final RedisMessenger redisMessenger,
      final RedisService redisService
  ) {
    super(redisMessenger, redisService, NetworkServerSettings.class, "network-server-settings");
  }


}

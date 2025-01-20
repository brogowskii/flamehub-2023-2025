package io.github.flamehub.commons.redis.cache;

import io.github.flamehub.commons.messenger.packet.PacketHandler;

public final class CacheHandler {

  private final RedisCache<?, ?> redisCache;

  public CacheHandler(final RedisCache<?, ?> redisCache) {
    this.redisCache = redisCache;
  }

  @PacketHandler
  public void handle(final CacheUpdate update) {
    redisCache.invalidateLocally(update.getPid(), update.getKey());

  }

}

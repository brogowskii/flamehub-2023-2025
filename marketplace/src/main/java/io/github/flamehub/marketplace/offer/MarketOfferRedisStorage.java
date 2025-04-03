package io.github.flamehub.marketplace.offer;

import io.github.flamehub.commons.redis.RedisService;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.lettuce.core.api.StatefulRedisConnection;
import java.util.UUID;

public final class MarketOfferRedisStorage {

  private final RedisService redisService;
  private final NetworkServerCache networkServerCache;

  public MarketOfferRedisStorage(
      final RedisService redisService,
      final NetworkServerCache networkServerCache) {
    this.redisService = redisService;
    this.networkServerCache = networkServerCache;
  }

  public void add(final UUID offerUUID) {
    try (final StatefulRedisConnection<String, String> connection = redisService.getClient()
        .connect()) {
      connection.sync().sadd(networkServerCache.getCurrent().getCategory() + "_market_offers",
          offerUUID.toString());
    }
  }

  public void remove(final UUID offerUUID) {
    try (final StatefulRedisConnection<String, String> connection = redisService.getClient()
        .connect()) {
      connection.sync().srem(networkServerCache.getCurrent().getCategory() + "_market_offers",
          offerUUID.toString());
    }
  }

  public boolean exists(final UUID offerUUID) {
    try (final StatefulRedisConnection<String, String> connection = redisService.getClient()
        .connect()) {
      return connection.sync()
          .sismember(networkServerCache.getCurrent().getCategory() + "_market_offers",
              offerUUID.toString());
    }
  }
}

package io.github.flamehub.marketplace.offer;

import io.github.flamehub.commons.redis.RedisService;
import io.github.flamehub.commons.server.NetworkServerCache;
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
    redisService.getClient().getSet(networkServerCache.getCurrent().getCategory() + "_market_offers").add(
        offerUUID.toString());
  }

  public void remove(final UUID offerUUID) {
    redisService.getClient().getSet(networkServerCache.getCurrent().getCategory() + "_market_offers").remove(
        offerUUID.toString());
  }

  public boolean exists(final UUID offerUUID) {
    return redisService.getClient().getSet(networkServerCache.getCurrent().getCategory() + "_market_offers").contains(
        offerUUID.toString());
  }
}

package io.github.flamehub.marketplace.offer;

import io.github.flamehub.commons.redis.RedisService;
import io.github.flamehub.commons.server.NetworkServerFacade;
import java.util.UUID;

public final class MarketOfferRedisStorage {

  private final RedisService redisService;
  private final NetworkServerFacade networkServerFacade;

  public MarketOfferRedisStorage(
      final RedisService redisService,
      final NetworkServerFacade networkServerFacade) {
    this.redisService = redisService;
    this.networkServerFacade = networkServerFacade;
  }

  public void add(final UUID offerUUID) {
    redisService.getClient().getSet(networkServerFacade.getCurrent().getCategory() + "_market_offers").add(
        offerUUID.toString());
  }

  public void remove(final UUID offerUUID) {
    redisService.getClient().getSet(networkServerFacade.getCurrent().getCategory() + "_market_offers").remove(
        offerUUID.toString());
  }

  public boolean exists(final UUID offerUUID) {
    return redisService.getClient().getSet(networkServerFacade.getCurrent().getCategory() + "_market_offers").contains(
        offerUUID.toString());
  }
}

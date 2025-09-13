package io.github.flamehub.coinflip.user;

import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.redis.RedisService;
import io.github.flamehub.commons.server.NetworkServerContext;
import io.github.flamehub.commons.user.UserRedisCache;
import io.github.flamehub.commons.user.UserRepository;

public final class CoinFlipUserCache extends UserRedisCache<CoinFlipUser> {

  public CoinFlipUserCache(
      final RedisMessenger redisMessenger,
      final RedisService redisService,
      final UserRepository<CoinFlipUser> userRepository
  ) {
    super(redisMessenger, redisService, CoinFlipUser.class, NetworkServerContext.CURRENT_CATEGORY + "-coinflip_users", userRepository);
  }
}

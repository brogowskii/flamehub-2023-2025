package io.github.flamehub.wallet.user;

import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.redis.RedisService;
import io.github.flamehub.commons.user.UserRedisCache;
import io.github.flamehub.commons.user.UserRepository;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

final class WalletUserCache extends UserRedisCache<WalletUser> {

  public WalletUserCache(final RedisMessenger redisMessenger,
      final RedisService redisService,
      final UserRepository<WalletUser> userRepository) {
    super(redisMessenger, redisService, WalletUser.class, "wallet-users", Integer.MAX_VALUE,
        Duration.of(1, ChronoUnit.MINUTES),
        userRepository);
  }
}

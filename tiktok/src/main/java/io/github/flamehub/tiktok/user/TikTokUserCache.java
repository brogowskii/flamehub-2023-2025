package io.github.flamehub.tiktok.user;

import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.redis.RedisService;
import io.github.flamehub.commons.user.UserRedisCache;
import io.github.flamehub.commons.user.UserRepository;

public final class TikTokUserCache extends UserRedisCache<TikTokUser> {

  public TikTokUserCache(
      final RedisMessenger redisMessenger,
      final RedisService redisService,
      final UserRepository<TikTokUser> userRepository) {
    super(redisMessenger, redisService, TikTokUser.class, "tiktok-users",
        userRepository);
  }
}

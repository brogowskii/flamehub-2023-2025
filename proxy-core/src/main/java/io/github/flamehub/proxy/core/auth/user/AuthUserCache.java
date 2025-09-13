package io.github.flamehub.proxy.core.auth.user;

import com.google.common.base.Strings;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.redis.RedisService;
import io.github.flamehub.commons.user.UserRedisCache;
import io.github.flamehub.commons.user.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public final class AuthUserCache extends UserRedisCache<AuthUser> {

  public AuthUserCache(
      final RedisMessenger redisMessenger,
      final RedisService redisService,
      final UserRepository<AuthUser> userRepository) {

    super(
        redisMessenger,
        redisService,
        AuthUser.class,
        "auth-users",
        userRepository
    );
  }


  @NotNull
  public List<AuthUser> findAccountsByIP(final String ip) {
    if (Strings.isNullOrEmpty(ip)) {
      return List.of();
    }

    return userRepository.loadAll("lastIP", ip)
        .stream()
        .filter(authUser -> authUser.isRegistered() || authUser.isPremium())
        .collect(Collectors.toList());
  }

}

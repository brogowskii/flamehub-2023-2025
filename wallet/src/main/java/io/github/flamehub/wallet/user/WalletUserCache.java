package io.github.flamehub.wallet.user;

import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.redis.RedisService;
import io.github.flamehub.commons.user.UserRedisCache;
import io.github.flamehub.commons.user.UserRepository;
import java.util.List;
import java.util.Objects;

final class WalletUserCache extends UserRedisCache<WalletUser> {

  public WalletUserCache(final RedisMessenger redisMessenger,
      final RedisService redisService,
      final UserRepository<WalletUser> userRepository
  ) {
    super(redisMessenger, redisService, WalletUser.class, "wallet-users", userRepository);
  }

  public void removeAllByName(final String name) {
    final List<WalletUser> usersToRemove = uuidByName.entrySet().stream()
        .filter(entry -> entry.getKey().equalsIgnoreCase(name.toLowerCase()))
        .map(entry -> get(entry.getValue()))
        .filter(Objects::nonNull)
        .toList();

    for (final WalletUser user : usersToRemove) {
      remove(user);
    }

    final List<WalletUser> usersFromDb = userRepository.loadAllIgnoreCase("name", name);
    for (final WalletUser user : usersFromDb) {
      remove(user);
    }
  }
}

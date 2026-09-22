package io.github.flamehub.coinflip;

import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.redis.RedisService;
import io.github.flamehub.commons.redis.cache.RedisCache;
import io.github.flamehub.commons.util.CompletableFutures;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.redisson.api.RLock;

public final class CoinFlipGameCache extends RedisCache<UUID, CoinFlipGame> {

  public CoinFlipGameCache(
      final RedisMessenger redisMessenger,
      final RedisService redisService,
      final String namespace
  ) {
    super(redisMessenger, redisService, CoinFlipGame.class, namespace, false);
  }

  public void update(final UUID uuid, final Consumer<CoinFlipGame> entity) {
    final RLock lock = cachedMap.getReadWriteLock(uuid.toString()).writeLock();
    lock.lock();
    try {

      final CoinFlipGame game = get(uuid);
      entity.accept(game);
      put(uuid, game);

    }
    finally {
      lock.forceUnlock();
    }
  }

  public int getPlayerGames(final UUID playerUUID) {
    int i = 0;
    for (final CoinFlipGame value : cachedMap.values()) {
      if (value.getCreator().getUniqueId().equals(playerUUID)) {
        i++;
      }
    }

    return i;
  }

}

package io.github.flamehub.coinflip;

import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.redis.RedisService;
import io.github.flamehub.commons.redis.cache.RedisCache;
import io.github.flamehub.commons.user.UserException;
import io.github.flamehub.commons.util.ThrowingConsumer;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class CoinFlipGameCache extends RedisCache<UUID, CoinFlipGame> {

  public CoinFlipGameCache(
      final RedisMessenger redisMessenger,
      final RedisService redisService,
      final String namespace
  ) {
    super(redisMessenger, redisService, CoinFlipGame.class, namespace, 0, Duration.ofSeconds(0));
  }

  public CompletableFuture<CoinFlipGame> mutate(
      final UUID uuid, final ThrowingConsumer<CoinFlipGame, Exception> mutator) {
    return supplyLocked(
        uuid,
        () -> {
          final CoinFlipGame game = get(uuid);
          if (game == null) {
            throw new UserException(
                "Game with UUID %s not found in redis-cache for mutation"
                    .formatted(uuid));
          }

          mutator.accept(game);
          set(uuid, game);
          return game;
        });
  }

}

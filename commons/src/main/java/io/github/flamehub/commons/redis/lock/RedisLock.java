package io.github.flamehub.commons.redis.lock;

import static io.github.flamehub.commons.util.CompletableFutures.exceptionallyCompose;
import static io.github.flamehub.commons.util.CompletableFutures.runLaterAsync;
import static io.github.flamehub.commons.util.CompletableFutures.supplyLaterAsync;
import static java.lang.Math.min;
import static java.time.Duration.ZERO;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static java.time.Instant.now;
import static java.time.Instant.ofEpochMilli;
import static java.util.UUID.randomUUID;
import static java.util.concurrent.ThreadLocalRandom.current;

import io.github.flamehub.commons.redis.storage.RedisStorage;
import io.github.flamehub.commons.redis.storage.StorageException;
import io.github.flamehub.commons.util.CompletableFutures;
import io.github.flamehub.commons.util.ThrowingSupplier;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public final class RedisLock {

  private final String key;
  private final String pid;
  private final RedisStorage storage;

  public RedisLock(final String key, final RedisStorage storage) {
    this.key = key;
    this.pid = randomUUID().toString();
    this.storage = storage;
  }

  public boolean acquire(final Duration ttl) throws RedisLockException {
    final String lockKey = getLockKey();
    try {
      final Instant now = now();
      final Instant expiresAt = now.plus(ttl);

      final RedisLockContext existingContext =
          storage.get(lockKey, RedisLockContext.class);
      if (existingContext == null || now.isAfter(ofEpochMilli(existingContext.getExpiresAt()))) {
        storage.set(lockKey, new RedisLockContext(pid, expiresAt.toEpochMilli()));
        return true;
      }

      return false;
    } catch (final StorageException exception) {
      throw new RedisLockException(
          "Failed to acquire lock %s with ttl %s".formatted(lockKey, ttl), exception);
    }
  }

  public boolean release() throws RedisLockException {
    final String lockKey = getLockKey();
    try {
      final RedisLockContext context = storage.get(lockKey, RedisLockContext.class);
      if (context == null) {
        return false;
      }

      if (context.getOwner().equals(pid)) {
        storage.remove(lockKey);
        return true;
      }

      return false;
    } catch (final StorageException exception) {
      throw new RedisLockException("Failed to release lock %s".formatted(lockKey), exception);
    }
  }

  public CompletableFuture<Void> execute(final Runnable runnable) {
    return execute(runnable, ofMillis(150L), ofSeconds(30L));
  }

  public CompletableFuture<Void> execute(
      final Runnable runnable, final Duration delay, final Duration until) {
    return execute(runnable, 0, delay, until, ZERO, now().plus(until));
  }

  private CompletableFuture<Void> execute(
      final Runnable runnable,
      final int retryCount,
      final Duration delay,
      final Duration until,
      final Duration backoffDelay,
      final Instant untilTime) {
    if (now().isAfter(untilTime)) {
      throw new RetryingException(retryCount);
    }
    return exceptionallyCompose(
        runLaterAsync(
            () -> {
              if (!acquire(until)) {
                throw new IllegalStateException(
                    "Failed to acquire lock within the specified time.");
              }
              try {
                runnable.run();
              } finally {
                release();
              }
            },
            calculateBackoffDelay(delay, until, retryCount + 1)),
        cause ->
            execute(
                runnable,
                retryCount + 1,
                delay,
                until,
                backoffDelay.plus(calculateBackoffDelay(delay, until, retryCount + 1)),
                untilTime))
        .toCompletableFuture();
  }

  public <T> CompletableFuture<T> supply(final ThrowingSupplier<T, Exception> supplier) {
    return supply(supplier, ofMillis(150L), ofSeconds(30L));
  }

  public <T> CompletableFuture<T> supply(
      final ThrowingSupplier<T, Exception> supplier, final Duration delay, final Duration until) {
    return supply(supplier, 0, delay, until, ZERO, now().plus(until));
  }

  private <T> CompletableFuture<T> supply(
      final ThrowingSupplier<T, Exception> supplier,
      final int retryCount,
      final Duration delay,
      final Duration until,
      final Duration backoffDelay,
      final Instant untilTime) {
    if (now().isAfter(untilTime)) {
      throw new RetryingException(retryCount);
    }
    return exceptionallyCompose(
        supplyLaterAsync(
            () -> {
              if (!acquire(until)) {
                throw new RedisLockException(
                    "Failed to acquire lock within the specified time.");
              }
              try {
                return supplier.get();
              } finally {
                release();
              }
            },
            calculateBackoffDelay(delay, until, retryCount + 1)),
        cause -> {

          if (!(cause instanceof RedisLockException)) {
            throw new RedisLockException("Failed to supply value because of unexcepted exception",
                cause);
          }

          return supply(
              supplier,
              retryCount + 1,
              delay,
              until,
              backoffDelay.plus(calculateBackoffDelay(delay, until, retryCount + 1)),
              untilTime);
        })
        .exceptionally(CompletableFutures::delegateCaughtException)
        .toCompletableFuture();
  }

  private String getLockKey() {
    return "lock-" + key;
  }

  private Duration calculateBackoffDelay(
      final Duration delay, final Duration until, final int retryCount) {
    long exponentialDelayMillis = delay.toMillis() * (1L << retryCount);
    if (exponentialDelayMillis < 0) {
      exponentialDelayMillis = Long.MAX_VALUE;
    }
    exponentialDelayMillis = min(exponentialDelayMillis, until.toMillis());

    final long randomPart =
        exponentialDelayMillis / 2 + current().nextInt((int) (exponentialDelayMillis / 2));

    return ofMillis(randomPart);
  }
}
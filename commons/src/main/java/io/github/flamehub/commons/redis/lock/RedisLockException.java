package io.github.flamehub.commons.redis.lock;

public final class RedisLockException extends RuntimeException {

  public RedisLockException(String message) {
    super(message);
  }

  public RedisLockException(String message, Throwable cause) {
    super(message, cause);
  }
}

package io.github.flamehub.commons.redis.lock;

public final class RedisLockContext {

  private final String owner;
  private final long expiresAt;

  public RedisLockContext(final String owner, final long expiresAt) {
    this.owner = owner;
    this.expiresAt = expiresAt;
  }

  public String getOwner() {
    return owner;
  }

  public long getExpiresAt() {
    return expiresAt;
  }
}

package io.github.flamehub.commons.redis.lock;

public final class RetryingException extends RuntimeException {

  public RetryingException(int count) {
    super("Retrying failed after " + count + " attempts");
  }

}

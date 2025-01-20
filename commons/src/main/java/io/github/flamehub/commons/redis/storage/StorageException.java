package io.github.flamehub.commons.redis.storage;

public final class StorageException extends RuntimeException {

  public StorageException(final String message) {
    super(message);
  }

  public StorageException(final String message, final Throwable cause) {
    super(message, cause);
  }

}

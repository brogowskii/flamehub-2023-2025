package io.github.flamehub.commons.user;

public class UserException extends RuntimeException {

  public UserException(final String message) {
    super(message);
  }

  public UserException(final String message, final Throwable cause) {
    super(message, cause);
  }

}

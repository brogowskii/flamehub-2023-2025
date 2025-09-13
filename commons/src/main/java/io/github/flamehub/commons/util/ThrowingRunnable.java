package io.github.flamehub.commons.util;

@FunctionalInterface
public interface ThrowingRunnable<E extends Exception> extends Runnable {

  void runWithException() throws E;

  @Override
  default void run() {
    try {
      runWithException();
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

}

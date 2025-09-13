package io.github.flamehub.commons.util;

import java.util.function.Supplier;

@FunctionalInterface
public interface ThrowingSupplier<T, E extends Exception> extends Supplier<T> {

  T getWithException() throws E;

  @Override
  default T get() {
    try {
      return getWithException();
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

}

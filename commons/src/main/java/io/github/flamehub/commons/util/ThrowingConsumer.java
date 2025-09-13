package io.github.flamehub.commons.util;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowingConsumer<T, E extends Exception> extends Consumer<T> {

  void acceptWithException(T t) throws Exception;

  @Override
  default void accept(final T t) {
    try {
      acceptWithException(t);
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }


}

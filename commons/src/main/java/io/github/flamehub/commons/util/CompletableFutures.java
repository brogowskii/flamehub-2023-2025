package io.github.flamehub.commons.util;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.logging.Level.SEVERE;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Logger;

public final class CompletableFutures {

  public static final CompletableFuture<Void> NIL = completedFuture(null);

  private static final ScheduledExecutorService SCHEDULER = new ScheduledThreadPoolExecutor(
      Runtime.getRuntime().availableProcessors());

  private CompletableFutures() {
  }

  public static <T> T delegateCaughtException(final Throwable cause) {
    return delegateCaughtException(Logger.getGlobal(), cause);
  }

  public static <T> T delegateCaughtException(final Logger logger, final Throwable cause) {
    logger.log(
        SEVERE, "Caught an exception in future execution: %s".formatted(cause.getMessage()), cause);
    return null;
  }

  public static <T> CompletableFuture<T> exceptionallyCompose(
      final CompletionStage<T> a, final Function<Throwable, CompletableFuture<T>> b) {
    return com.spotify.futures.CompletableFutures.exceptionallyCompose(a, b).toCompletableFuture();
  }

  public static CompletableFuture<Void> runLaterAsync(
      final ThrowingRunnable<Exception> runnable, final Duration delay) {
    return runAsync(runnable, delayedExecutor(delay.toMillis(), TimeUnit.MILLISECONDS));
  }

  public static Executor delayedExecutor(final long delay, final TimeUnit unit) {
    return delayedExecutor(delay, unit, ForkJoinPool.commonPool());
  }

  public static Executor delayedExecutor(
      final long delay, final TimeUnit unit, final Executor executor) {
    return task -> SCHEDULER.schedule(() -> executor.execute(task), delay, unit);
  }

  public static <T> CompletableFuture<T> supplyLaterAsync(
      final ThrowingSupplier<T, Exception> supplier, final Duration delay) {
    return supplyAsync(supplier, delayedExecutor(delay.toMillis(), TimeUnit.MILLISECONDS));
  }
}
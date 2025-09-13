package io.github.flamehub.commons.bukkit.dispatcher;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class FlameDispatcher {

  private final Plugin plugin;
  private final BukkitScheduler scheduler;

  public FlameDispatcher(final Plugin plugin, final BukkitScheduler scheduler) {
    this.plugin = plugin;
    this.scheduler = scheduler;
  }

  public void dispatchAsync(final Runnable runnable) {
    scheduler.runTaskAsynchronously(plugin, runnable);
  }

  public void dispatchAsyncLater(final Runnable runnable, final long ticks) {
    scheduler.runTaskLaterAsynchronously(plugin, runnable, ticks);
  }

  public void dispatch(final Runnable runnable) {
    scheduler.runTask(plugin, runnable);
  }

  public void dispatchLater(final Runnable runnable, final long ticks) {
    scheduler.runTaskLater(plugin, runnable, ticks);
  }

}

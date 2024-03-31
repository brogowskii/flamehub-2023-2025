package io.github.flamehub.commons.bukkit.dispatcher;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class FlameDispatcher {

    private final Plugin plugin;
    private final BukkitScheduler scheduler;

    public FlameDispatcher(Plugin plugin, BukkitScheduler scheduler) {
        this.plugin = plugin;
        this.scheduler = scheduler;
    }

    public void dispatchAsync(Runnable runnable) {
        this.scheduler.runTaskAsynchronously(this.plugin, runnable);
    }

    public void dispatchAsyncLater(Runnable runnable, long ticks) {
        this.scheduler.runTaskLaterAsynchronously(this.plugin, runnable, ticks);
    }

    public void dispatch(Runnable runnable) {
        this.scheduler.runTask(this.plugin, runnable);
    }

    public void dispatchLater(Runnable runnable, long ticks) {
        this.scheduler.runTaskLater(this.plugin, runnable, ticks);
    }

}

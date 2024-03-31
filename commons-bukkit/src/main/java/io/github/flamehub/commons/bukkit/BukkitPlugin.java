package io.github.flamehub.commons.bukkit;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.util.ServiceUtil;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitPlugin extends JavaPlugin {

    protected final FlameDispatcher flameDispatcher;

    public BukkitPlugin() {
        this.flameDispatcher = new FlameDispatcher(this, this.getServer().getScheduler());
    }

    public static <T> T getService(Class<T> type) {
        return ServiceUtil.getService(type);
    }

    public FlameDispatcher getFlameDispatcher() {
        return flameDispatcher;
    }
}

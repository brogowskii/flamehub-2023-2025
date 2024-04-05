package io.github.flamehub.commons.bukkit;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.util.ServiceUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitPlugin extends JavaPlugin {

    protected final FlameDispatcher flameDispatcher;

    public BukkitPlugin() {
        this.flameDispatcher = new FlameDispatcher(this, this.getServer().getScheduler());
    }

    public <T> T getService(Class<T> type) {
        ServicesManager servicesManager = Bukkit.getServer().getServicesManager();
        RegisteredServiceProvider<T> registration = servicesManager.getRegistration(type);
        if (registration != null) {
            return registration.getProvider();
        }

        throw new RuntimeException("Type: " + type.getSimpleName() + " is not registered!");
    }

    public FlameDispatcher getFlameDispatcher() {
        return flameDispatcher;
    }
}

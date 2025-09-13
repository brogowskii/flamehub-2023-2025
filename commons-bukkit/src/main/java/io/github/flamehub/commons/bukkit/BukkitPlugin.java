package io.github.flamehub.commons.bukkit;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitPlugin extends JavaPlugin {

  protected final FlameDispatcher flameDispatcher;

  public BukkitPlugin() {
    flameDispatcher = new FlameDispatcher(this, getServer().getScheduler());
  }

  public <T> T getService(final Class<T> type) {
    final ServicesManager servicesManager = Bukkit.getServer().getServicesManager();
    final RegisteredServiceProvider<T> registration = servicesManager.getRegistration(type);
    if (registration != null) {
      return registration.getProvider();
    }

    throw new RuntimeException("Type: " + type.getSimpleName() + " is not registered!");

  }

  public FlameDispatcher getFlameDispatcher() {
    return flameDispatcher;
  }
}

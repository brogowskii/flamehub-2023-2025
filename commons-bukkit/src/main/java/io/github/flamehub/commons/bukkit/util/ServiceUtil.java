package io.github.flamehub.commons.bukkit.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.jetbrains.annotations.NotNull;

public final class ServiceUtil {

  public ServiceUtil() {

  }

  @NotNull
  public static <T> T getService(Class<T> type) {
    ServicesManager servicesManager = Bukkit.getServer().getServicesManager();
    RegisteredServiceProvider<T> registration = servicesManager.getRegistration(type);
    if (registration != null) {
      return registration.getProvider();
    }

    throw new RuntimeException("Type: " + type.getSimpleName() + " is not registered!");
  }

}

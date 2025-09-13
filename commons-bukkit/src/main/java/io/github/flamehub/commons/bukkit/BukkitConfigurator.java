package io.github.flamehub.commons.bukkit;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class BukkitConfigurator {

  public void registerListeners(final Plugin plugin, final Listener... listeners) {
    for (final Listener listener : listeners) {
      plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }
  }

}

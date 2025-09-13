package io.github.flamehub.commons.bukkit;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import java.util.function.BiFunction;
import org.bukkit.plugin.Plugin;

public class BukkitBootstrapFactory<M extends BukkitModule> {

  private final BiFunction<Plugin, FlameDispatcher, M> biFunction;

  public BukkitBootstrapFactory(final BiFunction<Plugin, FlameDispatcher, M> biFunction) {
    this.biFunction = biFunction;
  }

  public M create(final Plugin plugin, final FlameDispatcher flameDispatcher) {
    return biFunction.apply(plugin, flameDispatcher);
  }

}

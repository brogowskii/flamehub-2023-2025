package io.github.flamehub.commons.bukkit;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import org.bukkit.plugin.Plugin;

import java.util.function.BiFunction;

public class BukkitBootstrapFactory<M extends BukkitModule> {

    private final BiFunction<Plugin, FlameDispatcher, M> biFunction;

    public BukkitBootstrapFactory(BiFunction<Plugin, FlameDispatcher, M> biFunction) {
        this.biFunction = biFunction;
    }

    public M create(Plugin plugin, FlameDispatcher flameDispatcher) {
        return biFunction.apply(plugin, flameDispatcher);
    }

}

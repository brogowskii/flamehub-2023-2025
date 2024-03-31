package io.github.flamehub.commons.bukkit;

public class BukkitBootstrap<M extends BukkitModule> extends BukkitPlugin {

    private M bukkitModule;
    private final BukkitBootstrapFactory<M> bukkitBootstrapFactory;

    public BukkitBootstrap(final BukkitBootstrapFactory<M> bukkitBootstrapFactory) {
        this.bukkitBootstrapFactory = bukkitBootstrapFactory;
    }

    @Override
    public void onEnable() {
        bukkitModule = bukkitBootstrapFactory.create(this, this.flameDispatcher);
        bukkitModule.onEnable();
    }

    @Override
    public void onDisable() {
        bukkitModule.onDisable();
    }

    @Override
    public void onLoad() {
    }
}

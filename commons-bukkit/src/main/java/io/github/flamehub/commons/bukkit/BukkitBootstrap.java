package io.github.flamehub.commons.bukkit;

public class BukkitBootstrap<M extends BukkitModule> extends BukkitPlugin {

  private final BukkitBootstrapFactory<M> bukkitBootstrapFactory;
  private M bukkitModule;

  public BukkitBootstrap(final BukkitBootstrapFactory<M> bukkitBootstrapFactory) {
    this.bukkitBootstrapFactory = bukkitBootstrapFactory;
  }

  @Override
  public void onEnable() {
    bukkitModule = bukkitBootstrapFactory.create(this, flameDispatcher);
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

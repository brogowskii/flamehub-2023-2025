package io.github.flamehub.spoof.tool;

import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.network.player.NetworkPlayer;

public class SpoofToolPlugin extends BukkitModule {

  @Override
  public void onEnable() {
    super.onEnable();
    getServer().getPluginManager().registerEvents(new SpoofToolListener(networkPlayerCache, networkServerCache), this);
  }

  @Override
  public void onDisable() {
    super.onDisable();

    for (final NetworkPlayer value : networkPlayerCache.values()) {
      if (!value.getServer().equals(networkServerCache.getCurrent().getName())) {
        continue;
      }

      if (value.getProxy().equals("null")) {
        networkPlayerCache.delete(value);
      }
    }

  }
}
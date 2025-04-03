package io.github.flamehub.coinflip;

import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@FlameConfigProperties(name = "coinflip.json")
public final class CoinFlipConfig extends FlameConfig {

  private ItemStack currency = new ItemStack(Material.DIRT);

  public CoinFlipConfig() {
  }

  public CoinFlipConfig(final ItemStack currency) {
    this.currency = currency;
  }

  public ItemStack getCurrency() {
    return currency;
  }

  public void setCurrency(final ItemStack currency) {
    this.currency = currency;
  }
}

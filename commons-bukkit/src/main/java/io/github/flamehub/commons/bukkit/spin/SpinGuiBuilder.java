package io.github.flamehub.commons.bukkit.spin;

import java.util.List;
import java.util.function.Consumer;
import org.bukkit.inventory.ItemStack;

public final class SpinGuiBuilder {

  private List<SpinReward> rewards;
  private Consumer<ItemStack> onSpinComplete;

  public SpinGuiBuilder rewards(final List<SpinReward> rewards) {
    this.rewards = rewards;
    return this;
  }

  public SpinGuiBuilder spinComplete(final Consumer<ItemStack> onSpinComplete) {
    this.onSpinComplete = onSpinComplete;
    return this;
  }

  public SpinGui build() {
    return new SpinGui(rewards, onSpinComplete);
  }
}
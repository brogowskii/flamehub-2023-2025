package io.github.flamehub.commons.bukkit.spin;

import io.github.flamehub.commons.bukkit.CommonsPlugin;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import java.util.List;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public final class SpinGui {

  private static final int INVENTORY_SIZE = 27;
  private static final int TOP_ROW_START = 0;
  private static final int TOP_ROW_END = 8;
  private static final int BOTTOM_ROW_START = 18;
  private static final int BOTTOM_ROW_END = 26;
  private static final int CENTER_SLOT = 13;
  private static final int SPINNER_START = 9;
  private static final int SPINNER_END = 17;
  private static final int HOPPER_SLOT = 22;

  private final List<SpinReward> rewards;
  private final Inventory inv;
  private final Consumer<ItemStack> onSpinComplete;
  private final double totalWeight;

  public SpinGui(
      final List<SpinReward> rewards,
      final Consumer<ItemStack> onSpinComplete
  ) {
    this.rewards = rewards;
    this.onSpinComplete = onSpinComplete;
    inv = Bukkit.createInventory(new SpinGuiHolder(), INVENTORY_SIZE,
        TextUtil.parse("&8&lLosowanie..."));

    double weight = 0;
    for (final SpinReward reward : rewards) {
      weight += reward.getChance();
    }
    totalWeight = weight;
  }

  public static SpinGuiBuilder builder() {
    return new SpinGuiBuilder();
  }

  public void spin(final Player player) {
    final ItemStack background = FlameItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE)
        .name(" ")
        .asItemStack();

    for (int i = TOP_ROW_START; i <= TOP_ROW_END; i++) {
      inv.setItem(i, background);
    }
    for (int i = BOTTOM_ROW_START; i <= BOTTOM_ROW_END; i++) {
      inv.setItem(i, background);
    }
    inv.setItem(HOPPER_SLOT, FlameItemBuilder.of(Material.HOPPER).asItemStack());
    shiftItems();
    player.openInventory(inv);

    new BukkitRunnable() {
      final int maxTicks = 100;
      int ticksPassed;
      int shiftCounter;
      int shiftDelay = 4;

      @Override
      public void run() {
        if (ticksPassed >= maxTicks) {
          final ItemStack selectedItem = inv.getItem(CENTER_SLOT);
          if (selectedItem != null) {
            onSpinComplete.accept(selectedItem.clone());
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
          }
          cancel();
          return;
        }

        if (ticksPassed >= 80) {
          shiftDelay = 10;
        } else if (ticksPassed >= 70) {
          shiftDelay = 6;
        } else if (ticksPassed >= 50) {
          shiftDelay = 5;
        }

        shiftCounter++;
        if (shiftCounter >= shiftDelay) {
          shiftItems();
          player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
          shiftCounter = 0;
        }

        player.updateInventory();
        ticksPassed++;
      }
    }.runTaskTimer(CommonsPlugin.getInstance(), 0L, 1L);
  }

  private void shiftItems() {
    for (int i = SPINNER_END; i > SPINNER_START; i--) {
      final ItemStack previous = inv.getItem(i - 1);
      inv.setItem(i, previous);
    }
    inv.setItem(SPINNER_START, getRandomItem());
  }

  private ItemStack getRandomItem() {
    double randomValue = Math.random() * totalWeight;
    for (final SpinReward reward : rewards) {
      randomValue -= reward.getChance();
      if (randomValue <= 0) {
        return reward.getItemStack().clone();
      }
    }
    return rewards.getLast().getItemStack().clone();
  }

}

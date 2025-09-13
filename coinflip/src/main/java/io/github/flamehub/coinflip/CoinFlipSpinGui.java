package io.github.flamehub.coinflip;

import io.github.flamehub.commons.bukkit.CommonsPlugin;
import io.github.flamehub.commons.bukkit.spin.SpinGuiHolder;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import java.util.Random;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public final class CoinFlipSpinGui {

  private static final int INVENTORY_SIZE = 27;
  private static final int CENTER_SLOT = 13;
  private static final int HOPPER_SLOT = 22;

  private final Inventory inv;
  private final Consumer<ItemStack> onSpinComplete;
  private final ItemStack winningHead;
  private final ItemStack losingHead;
  private final Random random = new Random();

  public CoinFlipSpinGui(
      final ItemStack winningHead,
      final ItemStack losingHead,
      final Consumer<ItemStack> onSpinComplete
  ) {
    this.winningHead = winningHead;
    this.losingHead = losingHead;
    this.onSpinComplete = onSpinComplete;
    inv = Bukkit.createInventory(new SpinGuiHolder(), INVENTORY_SIZE,
        TextUtil.parse("&8&lLosowanie..."));
  }

  public void spin(final Player player) {
    final ItemStack bg = FlameItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").asItemStack();
    for (int i = 0; i < INVENTORY_SIZE; i++) {
      inv.setItem(i, bg);
    }
    inv.setItem(HOPPER_SLOT, FlameItemBuilder.of(Material.HOPPER).asItemStack());
    player.openInventory(inv);

    final int totalFlips = 20;
    new BukkitRunnable() {
      int flipsDone;
      int shiftCounter;
      int shiftDelay = 2;  // start szybko

      @Override
      public void run() {
        if (flipsDone >= 15) {
          shiftDelay = 8;
        } else if (flipsDone >= 10) {
          shiftDelay = 4;
        }

        shiftCounter++;
        if (shiftCounter >= shiftDelay) {
          final ItemStack toShow;
          if (flipsDone == totalFlips - 1) {
            toShow = winningHead.clone();
          } else {
            toShow = (flipsDone % 2 == 0 ? losingHead : winningHead).clone();
          }

          inv.setItem(CENTER_SLOT, toShow);
          player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
          player.updateInventory();

          flipsDone++;
          shiftCounter = 0;

          if (flipsDone >= totalFlips) {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
            onSpinComplete.accept(winningHead.clone());
            cancel();
          }
        }
      }
    }.runTaskTimer(CommonsPlugin.getInstance(), 0L, 1L);
  }
}

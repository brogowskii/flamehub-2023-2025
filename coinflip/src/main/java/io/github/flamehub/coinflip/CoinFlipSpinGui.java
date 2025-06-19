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
  private static final int TOP_ROW_START = 0;
  private static final int TOP_ROW_END = 8;
  private static final int BOTTOM_ROW_START = 18;
  private static final int BOTTOM_ROW_END = 26;
  private static final int CENTER_SLOT = 13;
  private static final int SPINNER_START = 9;
  private static final int SPINNER_END = 17;
  private static final int HOPPER_SLOT = 22;

  private final Inventory inv;
  private final Consumer<ItemStack> onSpinComplete;
  private final ItemStack winningHead;
  private final ItemStack losingHead;
  private final Random random = new Random();

  public CoinFlipSpinGui(ItemStack winningHead, ItemStack losingHead, Consumer<ItemStack> onSpinComplete) {
    this.winningHead = winningHead;
    this.losingHead = losingHead;
    this.onSpinComplete = onSpinComplete;
    this.inv = Bukkit.createInventory(new SpinGuiHolder(), INVENTORY_SIZE, TextUtil.parse("&8&lLosowanie..."));
  }

  public void spin(Player player) {
    // przygotowanie tła i otwarcie inventory
    ItemStack bg = FlameItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").asItemStack();
    for (int i = 0; i < INVENTORY_SIZE; i++) {
      inv.setItem(i, bg);
    }
    inv.setItem(HOPPER_SLOT, FlameItemBuilder.of(Material.HOPPER).asItemStack());
    player.openInventory(inv);

    final int totalFlips = 20;
    new BukkitRunnable() {
      int flipsDone = 0;
      int shiftCounter = 0;
      int shiftDelay = 2;  // start szybko

      @Override
      public void run() {
        // dynamiczne zwalnianie: po 10 flipach trochę zwalniamy, po 15 jeszcze bardziej
        if (flipsDone >= 15) {
          shiftDelay = 8;
        } else if (flipsDone >= 10) {
          shiftDelay = 4;
        }

        shiftCounter++;
        if (shiftCounter >= shiftDelay) {
          // wybieramy co pokazać: na przemian przegrana/wygrana, ale ostatnia zawsze wygrana
          ItemStack toShow;
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
            // skończyliśmy – już mamy winningHead na środku
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
            onSpinComplete.accept(winningHead.clone());
            cancel();
          }
        }
      }
    }.runTaskTimer(CommonsPlugin.getInstance(), 0L, 1L);
  }
}

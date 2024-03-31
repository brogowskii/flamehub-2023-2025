package io.github.flamehub.crates.crate.battle;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.crates.crate.Crate;
import io.github.flamehub.crates.crate.CrateItem;
import io.github.flamehub.crates.crate.CrateSpinGuiHolder;

import java.util.ArrayList;
import java.util.List;

public class CrateBattleSpinGui {

    private final CrateBattle crateBattle;
    private final Plugin plugin;
    private final Inventory inv;

    private final List<Crate> crates;

    public CrateBattleSpinGui(CrateBattle crateBattle, Plugin plugin) {
        this.crateBattle = crateBattle;
        this.plugin = plugin;

        this.inv = Bukkit.createInventory(new CrateSpinGuiHolder(), 45, TextUtil.parse("&8&lLosowanie..."));
        this.crates = new ArrayList<>(crateBattle.getCrates());
    }

    public void spin() {

        for (int i = 0; i < 9; i++) {
            if (crates.size() - 1 < i) {
                continue;
            }

            Crate crate = crates.get(i);
            if (crate == null) {
                continue;
            }

            inv.setItem(i, FlameItemBuilder.of(Material.RED_SHULKER_BOX).asItemStack());
        }

        for (int i = 9; i < 18; i++) {
            inv.setItem(i, FlameItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).asItemStack());
        }

        for (int i = 36; i < 45; i++) {
            inv.setItem(i, FlameItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).asItemStack());
        }

        Player creator = crateBattle.getCreator();
        Player opponent = crateBattle.getOpponent();


        shiftItems();
        creator.openInventory(inv);
        opponent.openInventory(inv);
        new BukkitRunnable() {
            int ticksPassed = 0;
            int shiftCounter = 0;
            final int maxTicks = 100;
            int shiftEveryNTicks = 4;

            @Override
            public void run() {
                if (ticksPassed >= maxTicks) {

                    if (crates.size() - 1 >= 0) {
                        crates.remove(0);
                        creator.closeInventory();
                        opponent.closeInventory();
                        spin();
                        this.cancel();
                        return;
                    }

                    this.cancel();
                    return;
                }

                if (ticksPassed >= 80) {
                    shiftEveryNTicks = 10;
                }
                if (ticksPassed >= 70) {
                    shiftEveryNTicks = 6;
                }
                else if (ticksPassed >= 50) {
                    shiftEveryNTicks = 5;
                }

                shiftCounter++;
                if (shiftCounter >= shiftEveryNTicks) {
                    shiftItems();
                    creator.playSound(creator.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
                    opponent.playSound(opponent.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
                    shiftCounter = 0;
                }

                opponent.updateInventory();
                creator.updateInventory();
                ticksPassed++;
            }
        }.runTaskTimer(this.plugin, 0L, 1L);
    }

    private void shiftItems() {

        shiftItems2(24, 20);
        shiftItems2(33, 29);
        inv.setItem(20, FlameItemBuilder.of(getRandomItem().clone()).asItemStack());
        inv.setItem(29, FlameItemBuilder.of(getRandomItem().clone()).asItemStack());
    }

    private void shiftItems2(int a, int b) {
        for (int i = a; i >= b; i--) {
            ItemStack guiItem = inv.getItem(i - 1);
            if (guiItem == null) {
                continue;
            }

            inv.setItem(i, guiItem);
        }
    }

    private ItemStack getRandomItem() {
        double totalWeight = 0.0d;
        List<CrateItem> rewards = List.copyOf(crates.get(0).getItems());
        for (CrateItem reward : rewards) {
            totalWeight += reward.getChance();
        }
        int index = -1;
        double random = Math.random() * totalWeight;
        for (int i = 0; i < rewards.size(); i++) {
            random -= rewards.get(i).getChance();
            if (random <= 0.0d) {
                index = i;
                break;
            }
        }
        return rewards.get(index).getItemStack();
    }
}

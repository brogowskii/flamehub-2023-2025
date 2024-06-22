package io.github.flamehub.crates;

import io.github.flamehub.commons.bukkit.CommonsPlugin;
import io.github.flamehub.commons.network.message.NetworkMessageFilter;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.InventoryUtil;

import java.util.List;

public class CrateSpinGui {

    private final Plugin plugin;
    private final BukkitMessagesService messagesService;
    private final Crate crate;
    private final Inventory inv;

    public CrateSpinGui(Plugin plugin, BukkitMessagesService messagesService, Crate crate) {
        this.plugin = plugin;
        this.messagesService = messagesService;
        this.crate = crate;

        this.inv = Bukkit.createInventory(new CrateSpinGuiHolder(), 27, TextUtil.parse("&8&lLosowanie..."));
    }

    public void spin(Player player) {
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, FlameItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).asItemStack());
        }

        for (int i = 18; i < 27; i++) {
            inv.setItem(i, FlameItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).asItemStack());
        }

        inv.setItem(22, FlameItemBuilder.of(Material.HOPPER).asItemStack());


        shiftItems();
        player.openInventory(inv);
        new BukkitRunnable() {
            int ticksPassed = 0;
            int shiftCounter = 0; // Nowa zmienna
            final int maxTicks = 100; // 5 seconds (5 * 20 ticks)
            int shiftEveryNTicks = 4; // Zmieniaj przedmioty co 3 ticki

            @Override
            public void run() {
                if (ticksPassed >= maxTicks) {
                    ItemStack item = inv.getItem(13);
                    ItemStack clone = item.clone();
                    InventoryUtil.addItem(player, clone);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);

                    String drawnMessage = messagesService.message("crate.open." + crate.getId())
                            .with("player", player.getName())
                            .with("crate_name", crate.getGuiName())
                            .applyFirst();
                    CommonsPlugin.getInstance().getFlameDispatcher().dispatchAsync(() -> {

                        CommonsPlugin.getInstance().getNetworkMessageService().send(
                                drawnMessage,
                                NetworkMessageFilter.builder()
                                        .targetServerCategory(CommonsPlugin.getInstance().getNetworkServerCache().getCurrent().getCategory())
                                        .build(),
                                NetworkMessageType.CHAT
                        );

                    });

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
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
                    shiftCounter = 0;
                }

                player.updateInventory();
                ticksPassed++;
            }
        }.runTaskTimer(this.plugin, 0L, 1L);
    }

    private void shiftItems() {
        for (int i = 17; i >= 9; i--) {
            ItemStack guiItem = inv.getItem(i - 1);
            if (guiItem == null) {
                continue;
            }

            inv.setItem(i, guiItem);
        }
        inv.setItem(9, FlameItemBuilder.of(getRandomItem().clone()).asItemStack());
    }

    private ItemStack getRandomItem() {
        double totalWeight = 0.0d;
        List<CrateItem> rewards = List.copyOf(crate.getItems());
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

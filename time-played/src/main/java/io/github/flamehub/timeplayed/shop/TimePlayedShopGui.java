package io.github.flamehub.timeplayed.shop;

import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import io.github.flamehub.commons.bukkit.text.TextBuilder;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.InventoryUtil;
import io.github.flamehub.timeplayed.user.TimePlayedUser;
import io.github.flamehub.timeplayed.user.TimePlayedUserCache;

import java.util.Arrays;
import java.util.Map;

public final class TimePlayedShopGui {

    private final TimePlayedShopConfig timePlayedShopConfig;
    private final TimePlayedUserCache timePlayedUserCache;

    public TimePlayedShopGui(TimePlayedShopConfig timePlayedShopConfig, TimePlayedUserCache timePlayedUserCache) {
        this.timePlayedShopConfig = timePlayedShopConfig;
        this.timePlayedUserCache = timePlayedUserCache;
    }

    public void open(Player player) {
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
        Gui gui = Gui.gui()
                .rows(5)
                .title(TextUtil.parse("&#BE0FB9⌚ &8| &#BE0FB9&ls&#C918C4&lᴋ&#D320CE&lʟ&#DE29D9&lᴇ&#E831E3&lᴘ &#F33AEE&lᴢ&#E831E3&lᴀ &#DE29D9&lᴄ&#D320CE&lᴢ&#C918C4&lᴀ&#BE0FB9&ls"))
                .disableAllInteractions()
                .create();
        fillGui5(gui);

        TimePlayedUser timePlayedUser = this.timePlayedUserCache.findByUniqueId(player.getUniqueId());
        gui.setItem(1, 5, FlameItemBuilder.of(Material.CLOCK)
                .name("&5&lMonety czasu")
                .lore(
                        "",
                        "&5✪ &dJak zdobyć monety czasu?",
                        " &8&l┣ &7Wystarczy po prostu grać na serwerze.",
                        " &8&l┗ &51 moneta czasu &7odpowiada &d5 minut gry.",
                        "",
                        " &7Posiadasz: &5★" + timePlayedUser.getCoins() + " monet czasu",
                        ""
                )
                .asGuiItem());
        for (Map.Entry<Integer, TimePlayedShopItem> entry : this.timePlayedShopConfig.getItemsBySlot().entrySet()) {
            Integer key = entry.getKey();
            TimePlayedShopItem value = entry.getValue();
            gui.setItem(key, FlameItemBuilder.of(value.getItemStack().clone())
                    .appendLore(
                            "",
                            "&5✪ &dInformacje:",
                            " &8&l┣ &7Cena przedmiotu: &5★" + value.getPrice() + " monet czasu",
                            " &8&l┗ &7Twój stan konta: &5★" + timePlayedUser.getCoins() + " monet czasu",
                            "",
                            " &7Kliknij, aby &fzakupić &7ten przedmiot.",
                            ""

                    )
                    .asGuiItem(event -> {

                        if (timePlayedUser.getCoins() < value.getPrice()) {
                            TextBuilder.builder()
                                    .text("&cNie stać cię na zakup tego przedmiotu!")
                                    .send(player);
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                            gui.close(player);
                            return;
                        }

                        gui.close(player);
                        InventoryUtil.addItem(player, value.getItemStack());
                        timePlayedUser.setCoins(timePlayedUser.getCoins() - value.getPrice());
                        timePlayedUser.setNeedUpdate(true);
                        TextBuilder.builder()
                                .text("&aPomyślnie zakupiono ten przedmiot!")
                                .send(player);
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);

                    }));

        }


        gui.open(player);
    }

    private void fillGui5(BaseGui gui) {
        gui.getFiller().fillBorder(FlameItemBuilder.of(Material.WHITE_STAINED_GLASS_PANE).name(" ").asGuiItem());
        gui.setItem(Arrays.asList(0, 8, 36, 44), FlameItemBuilder.of(Material.MAGENTA_STAINED_GLASS_PANE).name(" ").asGuiItem());
        gui.setItem(Arrays.asList(1, 7, 9, 17, 27, 35, 37, 43), FlameItemBuilder.of(Material.PURPLE_STAINED_GLASS_PANE).name(" ").asGuiItem());
        gui.setItem(40, FlameItemBuilder.of(Material.AIR).asGuiItem());
        gui.setItem(4, FlameItemBuilder.of(Material.AIR).asGuiItem());
    }

}

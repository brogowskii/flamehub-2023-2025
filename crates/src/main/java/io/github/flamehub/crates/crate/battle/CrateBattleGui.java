package io.github.flamehub.crates.crate.battle;

import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import io.github.flamehub.commons.bukkit.text.TextBuilder;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.GuiHelper;
import io.github.flamehub.crates.crate.Crate;

import java.util.stream.Collectors;

public final class CrateBattleGui {

    private final Plugin plugin;
    private final Player player;
    private final CrateBattleCache crateBattleCache;

    public CrateBattleGui(Plugin plugin, Player player, CrateBattleCache crateBattleCache) {
        this.plugin = plugin;
        this.player = player;
        this.crateBattleCache = crateBattleCache;
    }

    public void openSearcher() {

        Gui gui = Gui.gui()
                .rows(6)
                .title(TextUtil.parse("&8&lWyszukiwarka bitew"))
                .disableAllInteractions()
                .create();
        GuiHelper.fillGui6(gui);

        for (CrateBattle value : this.crateBattleCache.values()) {

            gui.addItem(FlameItemBuilder.of(Material.PLAYER_HEAD)
                    .name("&7Case battle gracza &6" + value.getCreator().getName())
                    .appendLore(
                            TextBuilder.builder()
                                    .text(
                                            "",
                                            "&7Skrzynki: &6{CRATES}",
                                            "&7Status: {STATUS}"
                                    )
                                    .placeholder("{STATUS}", value.getOpponent() == null ? "&cOczekiwanie na przeciwnika" : "&aBitwa w toku")
                                    .placeholder("{CRATES}", value.getCrates().stream().map(Crate::getGuiName).collect(Collectors.joining("&7, &6")))
                                    .build()
                    )
                    .asGuiItem(event -> {

                        if (value.getOpponent() == null && value.getCreator().getUniqueId().equals(player.getUniqueId())) {
                            TextBuilder.builder()
                                    .text("&cNie możesz walczyć ze sobą!")
                                    .send(player);
                            return;
                        }

                        if (value.getOpponent() != null) {
                            TextBuilder.builder()
                                    .text("&cTa bitwa jest już w toku!")
                                    .send(player);
                            return;
                        }

                        if (value.getCreator() == null) {
                            TextBuilder.builder()
                                    .text("&cTwórca bitwy nie jest online!")
                                    .send(player);
                            return;
                        }

                        value.setOpponent(player);
                        new CrateBattleSpinGui(value, this.plugin).spin();


                    }));

        }

        gui.open(player);

    }

}

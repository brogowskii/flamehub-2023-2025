package io.github.flamehub.essentials.warp;

import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.commons.bukkit.teleport.TeleporterService;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.GuiHelper;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

final class WarpGui {

    private final WarpConfig warpConfig;
    private final TeleporterService teleporterService;

    WarpGui(final WarpConfig warpConfig, final TeleporterService teleporterService) {
        this.warpConfig = warpConfig;
        this.teleporterService = teleporterService;
    }

    public void open(final Player player) {
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 2f, 1f);
        final Gui gui = Gui.gui()
                .disableAllInteractions()
                .title(TextUtil.parse("&8&lLista warpów"))
                .rows(5)
                .create();
        GuiHelper.fillGui5(gui);

        for (final Warp warp : this.warpConfig.getWarpMap().values()) {
            gui.setItem(warp.getGuiSlot(), FlameItemBuilder.of(warp.getGuiIcon())
                    .name(warp.getGuiName())
                    .lore(warp.getGuiLore())
                    .glow()
                    .asGuiItem(event -> {
                        this.teleporterService.teleport(player, warp.getLocation().clone(), warp.getTeleportSeconds());
                        gui.close(player);
                    }));
        }

        gui.open(player);

    }

}

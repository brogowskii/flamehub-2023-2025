package io.github.flamehub.essentials.warp;

import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.commons.bukkit.teleport.TeleporterService;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

final class WarpGui {

  private final WarpFacade warpFacade;
  private final TeleporterService teleporterService;

  WarpGui(final WarpFacade warpFacade, final TeleporterService teleporterService) {
    this.warpFacade = warpFacade;
    this.teleporterService = teleporterService;
  }

  public static void fillGui5(BaseGui gui) {
    gui.getFiller()
        .fillBorder(FlameItemBuilder.of(Material.WHITE_STAINED_GLASS_PANE).name(" ").asGuiItem());
    gui.setItem(Arrays.asList(0, 8, 36, 44),
        FlameItemBuilder.of(Material.LIME_STAINED_GLASS_PANE).name(" ").asGuiItem());
    gui.setItem(Arrays.asList(1, 7, 9, 17, 27, 35, 37, 43),
        FlameItemBuilder.of(Material.GREEN_STAINED_GLASS_PANE).name(" ").asGuiItem());
    gui.setItem(40, FlameItemBuilder.of(Material.AIR).asGuiItem());
    gui.setItem(4, FlameItemBuilder.of(Material.AIR).asGuiItem());
  }

  public void open(final Player player) {
    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 2f, 1f);
    final Gui gui = Gui.gui()
        .disableAllInteractions()
        .title(TextUtil.parse(
            "&#40DD8A\uD83D\uDD31 &8| &#40DD8A&lʟ&#4AE191&lɪ&#55E498&ls&#5FE8A0&lᴛ&#69ECA7&lᴀ &#7EF3B5&lᴡ&#72EFAC&lᴀ&#65EAA4&lʀ&#59E69B&lᴘ&#4CE193&ló&#40DD8A&lᴡ"))
        .rows(5)
        .create();
    fillGui5(gui);

    for (final Warp warp : warpFacade.getWarps()) {
      gui.setItem(warp.getGuiSlot(), FlameItemBuilder.of(warp.getGuiIcon())
          .name(warp.getGuiName())
          .lore(warp.getGuiLore())
          .glow()
          .asGuiItem(event -> {
            teleporterService.teleport(player, warp.getLocation().clone(),
                warp.getTeleportSeconds());
            gui.close(player);
          }));
    }

    gui.open(player);

  }

}

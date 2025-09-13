package io.github.flamehub.flamebox;

import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.GuiHelper;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class FlameBoxGui {

  private final FlameBoxConfig flameBoxConfig;

  public FlameBoxGui(final FlameBoxConfig flameBoxConfig) {
    this.flameBoxConfig = flameBoxConfig;
  }

  public void open(final Player player) {
    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
    final Gui gui = Gui.gui()
        .title(TextUtil.parse(
            "&#C40505&lғ&#D40707&lʟ&#E40909&lᴀ&#F40B0B&lᴍ&#F40B0B&lᴇ&#E40909&lʙ&#D40707&lᴏ&#C40505&lx"))
        .rows(6)
        .disableAllInteractions()
        .create();
    GuiHelper.fillGui6(gui);

    gui.setItem(1, 5, FlameItemBuilder.of(Material.GLOW_ITEM_FRAME)
        .name("")
        .lore(
            "",
            " &fDoładowanie &6&lvPLN &fzakupisz na: &ewww.flamehub.pl",
            ""
        )
        .asGuiItem());

    for (final Map.Entry<Integer, FlameBoxDrop> entry : flameBoxConfig.getDrops().entrySet()) {

      final ItemStack itemStack = entry.getValue().getItemStack();
      gui.setItem(entry.getKey(), FlameItemBuilder.of(itemStack.clone()).asGuiItem());

    }

    gui.open(player);
  }

}

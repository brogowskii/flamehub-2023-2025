package io.github.flamehub.scratch;

import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.GuiHelper;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public final class ScratchDropGui {

    private final Player player;
    private final ScratchConfig scratchConfig;

    public ScratchDropGui(Player player, ScratchConfig scratchConfig) {
        this.player = player;
        this.scratchConfig = scratchConfig;
    }

    public void openPreview() {
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
        Gui gui = Gui.gui()
                .title(TextUtil.parse("&#236eff&l&nᴢ&#2e88ff&l&nᴅ&#3aa2ff&l&nʀ&#45bcff&l&nᴀ&#3aa2ff&l&nᴘ&#2e88ff&l&nᴋ&#236eff&l&nᴀ"))
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

        for (Map.Entry<Integer, ScratchDrop> entry : this.scratchConfig.getScratchCardDrops().entrySet()) {

            ItemStack itemStack = entry.getValue().getItemStack();
            gui.setItem(entry.getKey(), FlameItemBuilder.of(itemStack.clone()).asGuiItem());

        }

        gui.open(player);
    }

}

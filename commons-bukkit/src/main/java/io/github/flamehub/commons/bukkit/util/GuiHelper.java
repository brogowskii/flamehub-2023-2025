package io.github.flamehub.commons.bukkit.util;

import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.Material;

import java.util.Arrays;

public final class GuiHelper {

    public GuiHelper() {

    }

    public static void fillGui5(BaseGui gui) {
        gui.getFiller().fillBorder(FlameItemBuilder.of(Material.WHITE_STAINED_GLASS_PANE).name(" ").asGuiItem());
        gui.setItem(Arrays.asList(0, 8, 36, 44), FlameItemBuilder.of(Material.YELLOW_STAINED_GLASS_PANE).name(" ").asGuiItem());
        gui.setItem(Arrays.asList(1, 7, 9, 17, 27, 35, 37, 43), FlameItemBuilder.of(Material.ORANGE_STAINED_GLASS_PANE).name(" ").asGuiItem());
        gui.setItem(40, FlameItemBuilder.of(Material.AIR).asGuiItem());
        gui.setItem(4, FlameItemBuilder.of(Material.AIR).asGuiItem());
    }


    public static void fillGui6(BaseGui gui) {

        gui.getFiller().fillBorder(FlameItemBuilder.of(Material.WHITE_STAINED_GLASS_PANE).name(" ").asGuiItem());
        gui.setItem(Arrays.asList(0, 8, 45, 53), FlameItemBuilder.of(Material.YELLOW_STAINED_GLASS_PANE).name(" ").asGuiItem());
        gui.setItem(Arrays.asList(1, 7, 9, 17, 36, 44, 46, 52), FlameItemBuilder.of(Material.ORANGE_STAINED_GLASS_PANE).name(" ").asGuiItem());
        gui.setItem(49, FlameItemBuilder.of(Material.AIR).asGuiItem());
        gui.setItem(4, FlameItemBuilder.of(Material.AIR).asGuiItem());


    }

}

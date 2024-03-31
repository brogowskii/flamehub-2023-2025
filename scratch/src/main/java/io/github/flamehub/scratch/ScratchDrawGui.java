package io.github.flamehub.scratch;

import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.commons.bukkit.CommonsPlugin;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.InventoryUtil;
import io.github.flamehub.commons.network.message.NetworkMessageFilterBuilder;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ScratchDrawGui {

    private final Player player;
    private final ScratchConfig scratchConfig;
    private final NetworkMessageService networkMessageService;

    private boolean isTaken;
    private boolean isDrawnMain;
    private int drawnAdditional = 0;
    private Map<ItemStack, Integer> additionalItems = new HashMap<>();

    public ScratchDrawGui(Player player, ScratchConfig scratchConfig, NetworkMessageService networkMessageService) {
        this.player = player;
        this.scratchConfig = scratchConfig;
        this.networkMessageService = networkMessageService;
    }

    public void random() {
        Gui gui = Gui.gui()
                .title(TextUtil.parse("&8&lZdrapka..."))
                .rows(5)
                .disableAllInteractions()
                .create();

        gui.getFiller().fillBorder(FlameItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name("").asGuiItem());
        gui.setItem(List.of(13, 22, 31), FlameItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name("").asGuiItem());

        ItemStack scratch = this.scratchConfig.getScratchCardItem().clone();
        gui.setItem(3, 7, FlameItemBuilder.of(Material.GREEN_STAINED_GLASS_PANE)
                        .name("&#65c05e&lG&#67c460&lw&#69c762&la&#6bcb63&lr&#6dcf65&la&#6ed267&ln&#70d669&lt&#72da6a&lo&#74dd6c&lw&#76e16e&la&#74dd6c&ln&#72da6a&la &#70d669&lN&#6ed267&la&#6dcf65&lg&#6bcb63&lr&#69c762&lo&#67c460&ld&#65c05e&la")
                        .lore(
                                "",
                                "&fKliknij aby zdrapać gwarantowaną nagrodę!"
                        )
                        .glow()
                .asGuiItem(event -> {

                    if (drawnAdditional < 9) {
                        BukkitMessage.from("&cPierw zdrap po lewej stronie!").send(player);
                        return;
                    }

                    if (!isTaken) {
                        player.getInventory().removeItem(scratch);
                        isTaken = true;
                    }

                    ScratchDrop draw = draw();
                    int slot = event.getSlot();
                    gui.updateItem(slot, FlameItemBuilder.of(draw.getItemStack().clone()).asGuiItem());


                }));

        gui.setItem(List.of(10, 11, 12, 19, 20, 21, 28, 29, 30), FlameItemBuilder.of(Material.CYAN_STAINED_GLASS_PANE)
                .name("&#8fb7c0&lD&#93bcc5&lo&#96c0ca&ld&#9ac5cf&la&#9ecad4&lt&#a2ced9&lk&#a5d3de&lo&#a9d8e3&lw&#a9d8e3&la &#a5d3de&lN&#a2ced9&la&#9ecad4&lg&#9ac5cf&lr&#96c0ca&lo&#93bcc5&ld&#8fb7c0&la")
                .lore(
                        "",
                        " &bJak to działa?",
                        " &fMusisz zdrapać wszystkie 9 znajdujących się tutaj nagród",
                        " &fI ten item który wydropi ci przynajmniej 3 razy",
                        " &fdostaniesz do twojego ekwipunku!",
                        ""
                )
                .glow()
                .asGuiItem(event -> {

                    if (!isTaken) {
                        player.getInventory().removeItem(scratch);
                        isTaken = true;
                    }

                    int slot = event.getSlot();
                    ScratchDrop scratchDrop = this.scratchConfig.random();
                    ItemStack clone = scratchDrop.getItemStack().clone();
                    additionalItems.put(clone, additionalItems.getOrDefault(clone, 0) + 1);
                    drawnAdditional++;
                    gui.updateItem(slot, FlameItemBuilder.of(scratchDrop.getItemStack().clone()).asGuiItem());

                }));

        gui.setCloseGuiAction(event -> {
            if (isTaken) {
                if (this.drawnAdditional < 9 || (this.drawnAdditional == 9 && !isDrawnMain)) {
                    draw();
                }
            }

        });

        gui.setDefaultClickAction(event -> event.setCancelled(true));
        gui.open(player);
    }

    ScratchDrop draw() {
        ScratchDrop scratchDrop = this.scratchConfig.random();
        ItemStack clone = scratchDrop.getItemStack().clone();

        List<ItemStack> itemsToAdd = new ArrayList<>();
        itemsToAdd.add(clone);
        for (Map.Entry<ItemStack, Integer> entry : additionalItems.entrySet()) {
            if (entry.getValue() >= 3) {
                itemsToAdd.add(entry.getKey());
            }
        }

        for (ItemStack itemStack : itemsToAdd) {
            InventoryUtil.addItem(player, itemStack);
        }

        isDrawnMain = true;

        List<String> build = BukkitMessage
                .from("&7Gracz &f{PLAYER} &7otworzył &#236eff&lᴢ&#2e88ff&lᴅ&#3aa2ff&lʀ&#45bcff&lᴀ&#3aa2ff&lᴘ&#2e88ff&lᴋ&#236eff&lᴇ &7i wylosował z niej: {DROP}")
                .with("player", player.getName())
                .with("drop", itemsToAdd.stream()
                        .map(itemStack -> {

                            if (!itemStack.getItemMeta().hasDisplayName()) {
                                return itemStack.getType().toString().toUpperCase() + " &f&lx" + itemStack.getAmount();
                            }

                            return TextUtil.serialize(itemStack.getItemMeta().displayName()) + " &f&lx" + itemStack.getAmount();

                        })
                        .collect(Collectors.joining("&8, ")))
                .apply();

        this.networkMessageService.send(
                build,
                new NetworkMessageFilterBuilder()
                        .targetServerCategory(CommonsPlugin.getInstance().getNetworkServerCache().getCurrent().getCategory())
                        .build(),
                NetworkMessageType.CHAT
        );

        return scratchDrop;

    }

}

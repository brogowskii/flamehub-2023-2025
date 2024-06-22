package io.github.flamehub.crates;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import io.github.flamehub.commons.bukkit.CommonsPlugin;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.util.GuiHelper;
import io.github.flamehub.commons.network.message.NetworkMessageFilter;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.InventoryUtil;
import io.github.flamehub.commons.bukkit.util.TitleUtil;
import io.github.flamehub.commons.util.TimeUtil;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class CrateGui {

    private final Plugin plugin;
    private final BukkitMessagesService messagesService;
    private final CratesConfig cratesConfig;

    private final static Map<UUID, Instant> COOLDOWN_MAP = new HashMap<>();

    public CrateGui(Plugin plugin, BukkitMessagesService messagesService, CratesConfig cratesConfig) {
        this.plugin = plugin;
        this.messagesService = messagesService;
        this.cratesConfig = cratesConfig;
    }

    public void preview(Player player, Crate crate) {
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
        PaginatedGui gui = Gui.paginated()
                .rows(6)
                .pageSize(28)
                .title(TextUtil.parse(crate.getGuiName()))
                .disableAllInteractions()
                .create();
        GuiHelper.fillGui6(gui);

        gui.setItem(List.of(51, 50), FlameItemBuilder.of(Material.ORANGE_DYE)
                .name("&6&lOtwórz bez animacji")
                .asGuiItem(event -> {

                    Instant instant = COOLDOWN_MAP.get(player.getUniqueId());
                    if (instant != null && Instant.now().isBefore(instant)) {
                        BukkitMessage.from("&cPoczekaj chwilę przed następnym otworzeniem skrzynki!").send(player);
                        return;
                    }
                    COOLDOWN_MAP.put(player.getUniqueId(), Instant.now().plus(500, ChronoUnit.MILLIS));

                    if (!canOpen(crate, player)) {
                        return;
                    }

                    this.plugin.getServer().getPluginManager().callEvent(new CrateOpenEvent(player, crate.getId()));
                    draw(player, crate);

                }));

        gui.setItem(List.of(48, 47), FlameItemBuilder.of(Material.CYAN_DYE)
                .name("&b&lOtwórz z animacją")
                .asGuiItem(event -> {

                    if (!canOpen(crate, player)) {
                        return;
                    }

                    this.plugin.getServer().getPluginManager().callEvent(new CrateOpenEvent(player, crate.getId()));
                    new CrateSpinGui(this.plugin, messagesService, crate).spin(player);

                }));

        gui.setItem(6,5, FlameItemBuilder.of(Material.WHITE_STAINED_GLASS_PANE).name(" ").asGuiItem());

        gui.setItem(1, 5, FlameItemBuilder.of(Material.GLOW_ITEM_FRAME)
                .name("")
                .lore(
                        "",
                        " &fDoładowanie &6&lvPLN &fzakupisz na: &ewww.flamehub.pl",
                        ""
                )
                .asGuiItem());

        for (Map.Entry<Integer, CrateItem> entry : crate.getItemsBySlot().entrySet()) {

            CrateItem value = entry.getValue();
            Integer key = entry.getKey();
            gui.setItem(key, FlameItemBuilder.of(value.getItemStack().clone())
                    .asGuiItem());

        }

        gui.open(player);
    }

    public void draw(Player player, Crate crate) {
        Gui gui = Gui.gui()
                .rows(1)
                .disableAllInteractions()
                .title(TextUtil.parse("&8&lWylosowałeś:"))
                .create();

        CrateItem crateItem = this.cratesConfig.random(crate);
        ItemStack itemStack = crateItem.getItemStack();
        InventoryUtil.addItem(player, itemStack.clone());

        String drawnMessage = this.messagesService.message("crate.open." + crate.getId())
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

        gui.getFiller().fill(FlameItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).asGuiItem());

        gui.setItem(1, 5, FlameItemBuilder.of(itemStack.clone()).asGuiItem());
        gui.setItem(1, 7, FlameItemBuilder.of(Material.LIME_DYE)
                .name("&aOtwórz ponownie")
                .asGuiItem(event -> {

                    Instant instant = COOLDOWN_MAP.get(player.getUniqueId());
                    if (instant != null && Instant.now().isBefore(instant)) {
                        BukkitMessage.from("&cPoczekaj chwilę przed następnym otworzeniem skrzynki!").send(player);
                        return;
                    }
                    COOLDOWN_MAP.put(player.getUniqueId(), Instant.now().plus(500, ChronoUnit.MILLIS));

                    if (!canOpen(crate, player)) {
                        return;
                    }

                    this.plugin.getServer().getPluginManager().callEvent(new CrateOpenEvent(player, crate.getId()));
                    draw(player, crate);

                }));

        gui.open(player);
    }

    boolean canOpen(Crate crate, Player player) {
        if (!crate.isEnabled()) {

            TitleUtil.title(
                    player,
                    this.messagesService.getMessage("crate.is.disabled.title"),
                    this.messagesService.message("crate.is.disabled.subtitle")
                            .with("time", TimeUtil.formatTime(Duration.between(Instant.now(), crate.getEnabledFrom())))
                            .applyFirst(),
                    0, 40, 0);

            this.messagesService.message("crate.is.disabled")
                    .with("time", TimeUtil.formatTime(Duration.between(Instant.now(), crate.getEnabledFrom())))
                    .send(player);
            return false;
        }

        ItemStack clone = crate.getKey().clone();
        if (!player.getInventory().containsAtLeast(clone, 1)) {
            this.messagesService.message("crate.player.dont.have.key")
                    .with("crate_name", crate.getGuiName() == null ? "null" : crate.getGuiName())
                    .send(player);
            return false;
        }

        player.getInventory().removeItem(clone);
        return true;
    }



}
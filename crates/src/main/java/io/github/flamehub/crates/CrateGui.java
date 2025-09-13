package io.github.flamehub.crates;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import io.github.flamehub.commons.bukkit.CommonsPlugin;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.spin.SpinGui;
import io.github.flamehub.commons.bukkit.spin.SpinReward;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.InventoryUtil;
import io.github.flamehub.commons.bukkit.util.TitleUtil;
import io.github.flamehub.commons.network.message.NetworkMessageFilter;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import io.github.flamehub.commons.server.NetworkServerContext;
import io.github.flamehub.commons.server.NetworkServerFacade;
import io.github.flamehub.commons.util.TimeUtil;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class CrateGui {

  private final static Map<UUID, Instant> COOLDOWN_MAP = new HashMap<>();

  private final CratesConfig cratesConfig;

  private final NetworkServerFacade networkServerFacade;
  private final NetworkMessageService networkMessageService;
  private final BukkitMessagesService messagesService;

  public CrateGui(
      final CratesConfig cratesConfig,
      final NetworkServerFacade networkServerFacade,
      final NetworkMessageService networkMessageService,
      final BukkitMessagesService messagesService
  ) {
    this.cratesConfig = cratesConfig;
    this.networkServerFacade = networkServerFacade;
    this.networkMessageService = networkMessageService;
    this.messagesService = messagesService;
  }


  public void preview(final Player player, final Crate crate) {
    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
    final PaginatedGui gui = Gui.paginated()
        .rows(6)
        .pageSize(28)
        .title(TextUtil.parse(crate.getGuiName()))
        .disableAllInteractions()
        .create();

    gui.setItem(5, 1,
        FlameItemBuilder.of(Material.LIGHT_BLUE_STAINED_GLASS_PANE).name("").asGuiItem());
    gui.setItem(5, 2,
        FlameItemBuilder.of(Material.LIGHT_BLUE_STAINED_GLASS_PANE).name("").asGuiItem());
    gui.setItem(5, 3,
        FlameItemBuilder.of(Material.LIGHT_BLUE_STAINED_GLASS_PANE).name("").asGuiItem());
    gui.setItem(5, 4, FlameItemBuilder.of(Material.CYAN_STAINED_GLASS_PANE).name("").asGuiItem());

    gui.setItem(5, 6, FlameItemBuilder.of(Material.ORANGE_STAINED_GLASS_PANE).name("").asGuiItem());
    gui.setItem(5, 7, FlameItemBuilder.of(Material.YELLOW_STAINED_GLASS_PANE).name("").asGuiItem());
    gui.setItem(5, 8, FlameItemBuilder.of(Material.YELLOW_STAINED_GLASS_PANE).name("").asGuiItem());
    gui.setItem(5, 9, FlameItemBuilder.of(Material.YELLOW_STAINED_GLASS_PANE).name("").asGuiItem());

    gui.setItem(6, 1, FlameItemBuilder.of(Material.CYAN_STAINED_GLASS_PANE).name("").asGuiItem());
    gui.setItem(6, 9, FlameItemBuilder.of(Material.ORANGE_STAINED_GLASS_PANE).name("").asGuiItem());

    gui.setItem(6, 5, FlameItemBuilder.of(Material.BARRIER)
        .name("&c&lZamknij")
        .asGuiItem(event -> {
          gui.close(player);
        }));

    gui.setItem(List.of(51, 50, 52), FlameItemBuilder.of(Material.ORANGE_DYE)
        .name("&6&lOtwórz bez animacji")
        .asGuiItem(event -> {

          final Instant instant = COOLDOWN_MAP.get(player.getUniqueId());
          if (instant != null && Instant.now().isBefore(instant)) {
            BukkitMessage.from("&cPoczekaj chwilę przed następnym otworzeniem skrzynki!")
                .deliver(player);
            return;
          }
          COOLDOWN_MAP.put(player.getUniqueId(), Instant.now().plus(500, ChronoUnit.MILLIS));

          if (!canOpen(crate, player)) {
            return;
          }

          Bukkit.getPluginManager().callEvent(new CrateOpenEvent(player, crate.getId()));
          draw(player, crate);

        }));

    gui.setItem(List.of(48, 47, 46), FlameItemBuilder.of(Material.CYAN_DYE)
        .name("&b&lOtwórz z animacją")
        .asGuiItem(event -> {

          if (!canOpen(crate, player)) {
            return;
          }

          SpinGui.builder()
              .rewards(crate.getItems()
                  .stream()
                  .map(crateItem -> new SpinReward(crateItem.getItemStack(), crateItem.getChance()))
                  .toList())
              .spinComplete(itemStack -> {
                InventoryUtil.addItem(player, itemStack);

                final String drawnMessage = messagesService.message("crate.open." + crate.getId())
                    .with("player", player.getName())
                    .with("crate_name", crate.getGuiName())
                    .with("item", itemStack.getItemMeta().displayName() == null ? ""
                        : TextUtil.serialize(itemStack.getItemMeta().displayName()))
                    .applyFirst();

                networkMessageService.sendAsync(
                    drawnMessage,
                    NetworkMessageFilter.builder()
                        .idForHide("crates")
                        .targetServerCategory(networkServerFacade.getCurrent().getCategory())
                        .build(),
                    NetworkMessageType.CHAT
                );
              })
              .build()
              .spin(player);

          Bukkit.getPluginManager().callEvent(new CrateOpenEvent(player, crate.getId()));

        }));

    gui.setItem(5, 5, FlameItemBuilder.of(Material.GLOW_ITEM_FRAME)
        .name("")
        .lore(
            "",
            " &fDoładowanie &6&lvPLN &fzakupisz na: &ewww.flamehub.pl",
            ""
        )
        .asGuiItem());

    if (crate.hasRotation()) {

      for (final CrateItem item : crate.getItems()) {
        gui.addItem(FlameItemBuilder.of(item.getItemStack().clone())
            .appendLore(
                "",
                "&fSzansa: &e" + item.getChance() + "%",
                ""
            )
            .asGuiItem());
      }

      for (int i = 9; i < 18; i++) {
        gui.setItem(i, FlameItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE)
            .name(" ")
            .lore(
                "&6\uD83E\uDC69 &eItemy w aktualnej rotacji &6\uD83E\uDC69",
                "",
                " &fKolejna rotacja nastąpi za: &e" + (crate.hasRotation() ?
                    TimeUtil.formatTime(crate.getTimeUntilNextRotation()) : "Brak rotacji"),
                "",
                "&3\uD83E\uDC6B &bWszystkie itemy w skrzynce &3\uD83E\uDC6B"
            )
            .asGuiItem());
      }

      int i = 18;
      for (final CrateItem value : crate.getItemsBySlot().values()) {
        gui.setItem(i++, FlameItemBuilder.of(value.getItemStack().clone())
            .appendLore(
                "",
                "&fSzansa: &e" + value.getChance() + "%",
                ""
            )
            .asGuiItem());
      }

    } else {

      for (final Map.Entry<Integer, CrateItem> entry : crate.getItemsBySlot().entrySet()) {

        final CrateItem value = entry.getValue();
        final Integer key = entry.getKey();
        gui.setItem(key, FlameItemBuilder.of(value.getItemStack().clone())
            .appendLore(
                "",
                "&fSzansa: &e" + value.getChance() + "%",
                ""
            )
            .asGuiItem());

      }
    }

    gui.open(player);
  }

  public void draw(final Player player, final Crate crate) {
    final Gui gui = Gui.gui()
        .rows(1)
        .disableAllInteractions()
        .title(TextUtil.parse("&8&lWylosowałeś:"))
        .create();

    final CrateItem crateItem = cratesConfig.random(crate);
    final ItemStack itemStack = crateItem.getItemStack();
    InventoryUtil.addItem(player, itemStack.clone());

    final String drawnMessage = messagesService.message("crate.open." + crate.getId())
        .with("player", player.getName())
        .with("crate_name", crate.getGuiName())
        .with("item", itemStack.getItemMeta().displayName() == null ? ""
            : TextUtil.serialize(itemStack.getItemMeta().displayName()))
        .applyFirst();

    CommonsPlugin.getInstance().getNetworkMessageService().sendAsync(
        drawnMessage,
        NetworkMessageFilter.builder()
            .idForHide("crates")
            .targetServerCategory(NetworkServerContext.CURRENT_CATEGORY)
            .build(),
        NetworkMessageType.CHAT);

    gui.getFiller().fill(FlameItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).asGuiItem());

    gui.setItem(1, 5, FlameItemBuilder.of(itemStack.clone()).asGuiItem());
    gui.setItem(1, 7, FlameItemBuilder.of(Material.LIME_DYE)
        .name("&aOtwórz ponownie")
        .asGuiItem(event -> {

          final Instant instant = COOLDOWN_MAP.get(player.getUniqueId());
          if (instant != null && Instant.now().isBefore(instant)) {
            BukkitMessage.from("&cPoczekaj chwilę przed następnym otworzeniem skrzynki!")
                .deliver(player);
            return;
          }
          COOLDOWN_MAP.put(player.getUniqueId(), Instant.now().plus(500, ChronoUnit.MILLIS));

          if (!canOpen(crate, player)) {
            return;
          }

          Bukkit.getPluginManager().callEvent(new CrateOpenEvent(player, crate.getId()));
          draw(player, crate);

        }));

    gui.open(player);
  }

  boolean canOpen(final Crate crate, final Player player) {
    if (!crate.isEnabled()) {

      TitleUtil.title(
          player,
          messagesService.getMessage("crate.is.disabled.title"),
          messagesService.message("crate.is.disabled.subtitle")
              .with("time",
                  TimeUtil.formatTime(Duration.between(Instant.now(), crate.getEnabledFrom())))
              .applyFirst(),
          0, 40, 0);

      messagesService.message("crate.is.disabled")
          .with("time",
              TimeUtil.formatTime(Duration.between(Instant.now(), crate.getEnabledFrom())))
          .deliver(player);
      return false;
    }

    final ItemStack clone = crate.getKey().clone();
    if (!player.getInventory().containsAtLeast(clone, 1)) {
      messagesService.message("crate.player.dont.have.key")
          .with("crate_name", crate.getGuiName() == null ? "null" : crate.getGuiName())
          .deliver(player);
      return false;
    }

    player.getInventory().removeItem(clone);
    return true;
  }


}
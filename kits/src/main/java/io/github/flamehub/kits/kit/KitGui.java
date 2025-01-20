package io.github.flamehub.kits.kit;

import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.text.TextBuilder;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.GuiHelper;
import io.github.flamehub.commons.bukkit.util.InventoryUtil;
import io.github.flamehub.commons.util.TimeUtil;
import io.github.flamehub.kits.KitsConfig;
import io.github.flamehub.kits.user.KitUser;
import io.github.flamehub.kits.user.KitUserCache;
import io.github.flamehub.kits.user.KitUserRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class KitGui {

  private final FlameDispatcher flameDispatcher;
  private final KitsConfig kitConfig;
  private final KitUserCache kitUserCache;
  private final KitUserRepository kitUserRepository;

  public KitGui(FlameDispatcher flameDispatcher, KitsConfig kitConfig, KitUserCache kitUserCache,
      KitUserRepository kitUserRepository) {
    this.flameDispatcher = flameDispatcher;
    this.kitConfig = kitConfig;
    this.kitUserCache = kitUserCache;
    this.kitUserRepository = kitUserRepository;
  }

  public void open(Player player) {
    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
    Gui gui = Gui.gui()
        .rows(kitConfig.getRowsGui())
        .title(TextUtil.parse("&8&lDostępne zestawy"))
        .disableAllInteractions()
        .create();

    if (kitConfig.getRowsGui() == 6) {
      GuiHelper.fillGui6(gui);
    } else if (kitConfig.getRowsGui() == 5) {
      GuiHelper.fillGui5(gui);
    }

    KitUser kitUser = kitUserCache.findByUniqueId(player.getUniqueId());
    for (Kit kit : kitConfig.getKits()) {

      List<String> lore = TextBuilder.builder()
          .text(kit.getLore())
          .placeholder("{COOLDOWN}", TimeUtil.formatTime(kit.getCooldownDuration()))
          .build();

      gui.setItem(kit.getSlot(), FlameItemBuilder.of(kit.getIcon().clone())
          .name(kit.getGuiName())
          .lore(lore)
          .asGuiItem(inventoryClickEvent -> {
            openPreview(player, kitUser, kit);
          }));

    }

    gui.open(player);
  }

  public void openPreview(Player player, KitUser kitUser, Kit kit) {
    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
    Gui gui = Gui.gui()
        .rows(6)
        .title(TextUtil.parse(kit.getTitle()))
        .disableAllInteractions()
        .create();

    GuiHelper.fillGui6(gui);
    gui.setItem(6, 5, FlameItemBuilder.of(Material.RED_CONCRETE)
        .name("&c&lPowrót")
        .lore(
            "",
            " &7Kliknij aby powrócić na poprzednią stronę.",
            ""
        )
        .asGuiItem(inventoryClickEvent -> open(player)));

    boolean hasPermission = player.hasPermission(kit.getPermission());
    FlameItemBuilder itemBuilder = FlameItemBuilder.of(
        hasPermission ? Material.LIME_DYE : Material.RED_DYE);
    itemBuilder.name("&a&lOdbierz zestaw").lore("");

    if (hasPermission) {
      itemBuilder.appendLore(" &aKliknij tutaj aby &2odebrać &aten zestaw!");
    } else {
      itemBuilder.appendLore(" &cNie posiadasz uprawnień do odebrania tego zestawu!");
    }

    for (ItemStack item : kit.getItems()) {
      gui.addItem(FlameItemBuilder.of(item.clone()).asGuiItem());
    }

    gui.setItem(5, 8, itemBuilder.asGuiItem(event -> {

      if (!kit.isEnable()) {
        TextBuilder.builder()
            .text("&cTen zestaw został chwilowo wyłączony!")
            .send(player);
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
        return;
      }

      if (!hasPermission) {
        TextBuilder.builder()
            .text("&cNie posiadasz uprawnień do odebrania tego zestawu!")
            .send(player);
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
        return;
      }

      Instant kitCooldown = kitUser.getKitCooldown(kit.getName());
      if (kitCooldown.isAfter(Instant.now())) {
        TextBuilder.builder()
            .text("&cTen zestaw będziesz mógł odebrać dopiero za: &4{TIME}")
            .placeholder("{TIME}",
                TimeUtil.formatTime(Duration.between(Instant.now(), kitCooldown)))
            .send(player);
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
        return;
      }

      kitUser.addCooldown(kit.getName(), Instant.now().plus(kit.getCooldownDuration()));
      for (ItemStack item : kit.getItems()) {
        InventoryUtil.addItem(player, item.clone());
      }

      gui.close(player);
      player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
      flameDispatcher.dispatchAsync(() -> kitUserRepository.save(kitUser));

    }));

    gui.open(player);

  }

}

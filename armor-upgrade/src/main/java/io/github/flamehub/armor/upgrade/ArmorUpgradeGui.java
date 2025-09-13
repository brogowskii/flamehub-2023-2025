package io.github.flamehub.armor.upgrade;

import static io.github.flamehub.commons.bukkit.util.GuiHelper.fillGui5;
import static io.github.flamehub.commons.bukkit.util.GuiHelper.fillGui6;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import io.github.flamehub.commons.bukkit.CommonsPlugin;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.InventoryUtil;
import io.github.flamehub.commons.bukkit.util.NumberConverter;
import io.github.flamehub.commons.bukkit.util.SkullBuilder;
import io.github.flamehub.commons.bukkit.util.TitleUtil;
import io.github.flamehub.commons.util.RandomUtil;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class ArmorUpgradeGui {

  private final ArmorUpgradeService armorUpgradeService;
  private final ArmorUpgradeConfig armorUpgradeConfig;
  private final Economy economy;

  public ArmorUpgradeGui(
      final ArmorUpgradeService armorUpgradeService,
      final ArmorUpgradeConfig armorUpgradeConfig,
      final Economy economy
  ) {
    this.armorUpgradeService = armorUpgradeService;
    this.armorUpgradeConfig = armorUpgradeConfig;
    this.economy = economy;
  }


  public void open(final Player player) {
    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
    final Gui gui =
        Gui.gui()
            .title(TextUtil.parse(
                "&#CFB800&lᴜ&#D1BA03&lʟ&#D3BC06&lᴇ&#D4BE09&lᴘ&#D6C00C&ls&#D8C20F&lᴢ&#DAC311&lᴀ&#DCC514&lɴ&#DDC717&lɪ&#DFC91A&lᴇ &#E3CD20&lᴜ&#E5CF23&lᴢ&#E6D126&lʙ&#E8D329&lʀ&#EAD52C&lᴏ&#ECD62E&lᴊ&#EED831&lᴇ&#EFDA34&lɴ&#F1DC37&lɪ&#F3DE3A&lᴀ"))
            .rows(6)
            .disableAllInteractions()
            .disableOtherActions()
            .create();
    fillGui6(gui);

    for (final ArmorUpgradeType type : armorUpgradeConfig.getArmorUpgradeTypesById().values()) {

      final Map.Entry<ItemStack, Integer> highestLevel =
          armorUpgradeService.findHighestLevel(player, type);
      final int value = highestLevel.getValue();
      final int i = value + 1;

      final int highestLevelInType = armorUpgradeService.findHighestLevelInType(type);
      final int lowestLevelInType = armorUpgradeService.findLowestLevelInType(type);
      final boolean isMaxLevel = highestLevelInType == value;
      ArmorUpgrade armorUpgrade = armorUpgradeService.findByTypeAndLevel(type, i);
      if (armorUpgrade == null && !isMaxLevel) {
        armorUpgrade = armorUpgradeService.findByTypeAndLevel(type, lowestLevelInType);
      } else if (armorUpgrade == null) {
        armorUpgrade = armorUpgradeService.findByTypeAndLevel(type, value);
      }

      final ArmorUpgrade finalArmorUpgrade = armorUpgrade;
      gui.setItem(
          type.getSlot(),
          FlameItemBuilder.of(armorUpgrade.getItemStack().clone())
              .glow()
              .appendLore(
                  "",
                  "&f&l" + type.getName(),
                  "",
                  " &8▶ &7Poziom: &b★"
                      + armorUpgrade.getLevel()
                      + (isMaxLevel ? " &c(Maksymalny poziom)" : ""),
                  " &8▶ &7Maksymalny poziom: &3★" + highestLevelInType,
                  " &8▶ &7Koszt ulepszenia: &f$"
                      + NumberConverter.convertNumber(armorUpgrade.getCost())
                      + " &8(&7"
                      + armorUpgrade.getCost()
                      + "$&8)",
                  "",
                  "&7Kliknij &f&lLPM&7, aby ulepszyć.",
                  "&7Kliknij &f&lPPM&7, aby zobaczyć wszystkie poziomy.")
              .asGuiItem(
                  event -> {
                    if (event.isRightClick()) {
                      openAll(player, type);
                    } else if (event.isLeftClick()) {

                      if ("elytra".equalsIgnoreCase(finalArmorUpgrade.getType().getId())) {

                        if (CommonsPlugin.getInstance().getServerSettingConfig().isDisabled(player, "elytra_upgrade")) {
                          TitleUtil.title(player, " ", "&cUlepszanie Elytry jest aktualnie wyłączone", 0, 20, 20);
                          gui.close(player);
                          player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 3f, 1f);
                          return;
                        }

                      }

                      if (isMaxLevel) {
                        TitleUtil.title(player, " ", "&cMaksymalny poziom ulepszenia.", 0, 20, 20);
                        gui.close(player);
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 3f, 1f);
                        return;
                      }

                      if (finalArmorUpgrade.getLevel() == lowestLevelInType) {

                        if (!economy.has(player, finalArmorUpgrade.getCost())) {
                          TitleUtil.title(
                              player,
                              " ",
                              "&cNie posiadasz wystarczającej ilości pieniędzy.",
                              0,
                              20,
                              20);
                          gui.close(player);
                          player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 3f, 1f);
                          return;
                        }

                        economy.withdrawPlayer(player, finalArmorUpgrade.getCost());
                        TitleUtil.title(player, " ", "&aPomyślnie zakupiono!", 0, 20, 20);
                        InventoryUtil.addItem(player, finalArmorUpgrade.getItemStack().clone());
                        open(player);
                      } else {

                        openUpgrade(player, highestLevel.getKey(), finalArmorUpgrade);
                      }
                    }
                  }));
    }

    gui.open(player);
  }

  public Integer getSlotInPlayerInventoryByItemStack(final Player player,
      final ItemStack itemStack) {
    for (int i = 0; i < player.getInventory().getSize(); i++) {
      final ItemStack content = player.getInventory().getItem(i);
      if (content != null && content.isSimilar(itemStack)) {
        return i;
      }
    }
    return null;
  }

  public void openAll(final Player player, final ArmorUpgradeType type) {
    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
    final PaginatedGui gui =
        Gui.paginated()
            .title(TextUtil.parse(
                "&#CFB800&lᴜ&#D1BA03&lʟ&#D3BC06&lᴇ&#D4BE09&lᴘ&#D6C00C&ls&#D8C20F&lᴢ&#DAC311&lᴀ&#DCC514&lɴ&#DDC717&lɪ&#DFC91A&lᴇ &#E3CD20&lᴜ&#E5CF23&lᴢ&#E6D126&lʙ&#E8D329&lʀ&#EAD52C&lᴏ&#ECD62E&lᴊ&#EED831&lᴇ&#EFDA34&lɴ&#F1DC37&lɪ&#F3DE3A&lᴀ"))
            .rows(6)
            .pageSize(28)
            .disableAllInteractions()
            .disableOtherActions()
            .create();
    fillGui6(gui);

    final List<ArmorUpgrade> armorUpgrades = armorUpgradeService.getArmorUpgrades(type);
    armorUpgrades.sort(Comparator.comparingInt(ArmorUpgrade::getLevel));

    for (final ArmorUpgrade armorUpgrade : armorUpgrades) {
      gui.addItem(
          FlameItemBuilder.of(armorUpgrade.getItemStack().clone())
              .glow()
              .appendLore(
                  "",
                  " &8▶ &7Poziom: &b★" + armorUpgrade.getLevel(),
                  " &8▶ &7Koszt ulepszenia: &f$"
                      + NumberConverter.convertNumber(armorUpgrade.getCost())
                      + " &8(&7"
                      + armorUpgrade.getCost()
                      + "$&8)",
                  "")
              .asGuiItem());
    }

    gui.open(player);
  }

  public void openUpgrade(final Player player, final ItemStack eq, final ArmorUpgrade target) {

    final Gui gui =
        Gui.gui()
            .title(TextUtil.parse(
                "&#CFB800&lᴜ&#D1BA03&lʟ&#D3BC06&lᴇ&#D4BE09&lᴘ&#D6C00C&ls&#D8C20F&lᴢ&#DAC311&lᴀ&#DCC514&lɴ&#DDC717&lɪ&#DFC91A&lᴇ &#E3CD20&lᴜ&#E5CF23&lᴢ&#E6D126&lʙ&#E8D329&lʀ&#EAD52C&lᴏ&#ECD62E&lᴊ&#EED831&lᴇ&#EFDA34&lɴ&#F1DC37&lɪ&#F3DE3A&lᴀ"))
            .rows(5)
            .disableAllInteractions()
            .create();

    fillGui5(gui);

    gui.setItem(5, 5, FlameItemBuilder.of(Material.BARRIER).name("&c&lPowrót")
        .asGuiItem(event -> open(player)));

    gui.setItem(2, 3, FlameItemBuilder.of(eq.clone()).asGuiItem());
    gui.setItem(
        2,
        5,
        FlameItemBuilder.of(
                SkullBuilder.create(
                    "956a3618459e43b287b22b7e235ec699594546c6fcd6dc84bfca4cf30ab9311"))
            .name("")
            .asGuiItem());
    gui.setItem(2, 7, FlameItemBuilder.of(target.getItemStack().clone()).asGuiItem());

    gui.setItem(
        4,
        3,
        FlameItemBuilder.of(Material.CYAN_DYE)
            .name("")
            .lore(
                "&8▶ &7Szansa: &325%",
                "&8▶ &7Koszt ulepszenia: &f$"
                    + NumberConverter.convertNumber(target.getCost() * 0.5)
                    + " &8(&7"
                    + target.getCost() * 0.5
                    + "$&8)",
                "",
                "&3Kliknij, aby ulepszyć.")
            .asGuiItem(
                event -> {
                  if (update(player, eq, target, target.getCost() * 0.5, 25)) {
                    open(player);
                    return;
                  }
                  gui.close(player);
                }));

    gui.setItem(
        4,
        5,
        FlameItemBuilder.of(Material.LIGHT_BLUE_DYE)
            .name("")
            .lore(
                "&8▶ &7Szansa: &b50%",
                "&8▶ &7Koszt ulepszenia: &f$"
                    + NumberConverter.convertNumber(target.getCost() * 0.75)
                    + " &8(&7"
                    + target.getCost() * 0.75
                    + "$&8)",
                "",
                "&bKliknij, aby ulepszyć.")
            .asGuiItem(
                event -> {
                  if (update(player, eq, target, target.getCost() * 0.75, 50)) {
                    open(player);
                    return;
                  }

                  gui.close(player);
                }));

    gui.setItem(
        4,
        7,
        FlameItemBuilder.of(Material.WHITE_DYE)
            .name("")
            .lore(
                "&8▶ &7Szansa: &f100%",
                "&8▶ &7Koszt ulepszenia: &f$"
                    + NumberConverter.convertNumber(target.getCost())
                    + " &8(&7"
                    + target.getCost()
                    + "$&8)",
                "",
                "&fKliknij, aby ulepszyć.")
            .asGuiItem(
                event -> {
                  if (update(player, eq, target, target.getCost(), 100)) {
                    open(player);
                    return;
                  }

                  gui.close(player);
                }));

    gui.open(player);
  }

  boolean update(final Player player, final ItemStack eq, final ArmorUpgrade target,
      final double cost, final int chance) {

    if (!economy.has(player, cost)) {
      TitleUtil.title(player, " ", "&cNie posiadasz wystarczającej ilości pieniędzy.", 0, 20, 20);
      player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 3f, 1f);
      return false;
    }

    if (eq.getAmount() > 1) {
      TitleUtil.title(player, " ", "&cNie możesz ulepszyć zestackowanego przedmiotu.", 0, 50, 20);
      return false;
    }

    economy.withdrawPlayer(player, cost);
    if (!RandomUtil.getChance(chance)) {
      TitleUtil.title(player, " ", "&cNie udało się ulepszyć.", 0, 20, 20);
      return false;
    }

    final Integer slot = getSlotInPlayerInventoryByItemStack(player, eq);
    if (slot != null) {
      player.getInventory().setItem(slot, target.getItemStack().clone());
    }

    TitleUtil.title(player, " ", "&aPomyślnie ulepszono!", 0, 20, 20);
    return true;
  }

}

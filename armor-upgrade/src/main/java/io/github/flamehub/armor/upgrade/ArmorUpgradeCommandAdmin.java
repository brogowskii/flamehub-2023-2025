package io.github.flamehub.armor.upgrade;

import static io.github.flamehub.armor.upgrade.ArmorUpgradePlugin.CUSTOM_ARMOR_KEY;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.util.InventoryUtil;
import io.github.flamehub.commons.config.FlameConfigService;
import java.util.List;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

@Command(name = "armorupgradeadmin")
@Permission("server.commands.armorupgradeadmin")
public final class ArmorUpgradeCommandAdmin {


  private final FlameConfigService flameConfigService;
  private final ArmorUpgradeConfig armorUpgradeConfig;
  private final ArmorUpgradeService armorUpgradeService;

  public ArmorUpgradeCommandAdmin(
      final FlameConfigService flameConfigService,
      final ArmorUpgradeConfig armorUpgradeConfig,
      final ArmorUpgradeService armorUpgradeService
  ) {
    this.flameConfigService = flameConfigService;
    this.armorUpgradeConfig = armorUpgradeConfig;
    this.armorUpgradeService = armorUpgradeService;
  }

  @Execute(name = "fix")
  void fix(@Context final CommandSender sender) {

    for (final Map.Entry<String, List<ArmorUpgrade>> armorUpgradeTypeListEntry : armorUpgradeConfig.getArmorUpgradeTypeListMap()
        .entrySet()) {
      for (final ArmorUpgrade armorUpgrade : armorUpgradeTypeListEntry.getValue()) {
        final ItemStack itemStack = armorUpgrade.getItemStack();
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta.getPersistentDataContainer().has(CUSTOM_ARMOR_KEY)) {
          continue;
        }
        itemMeta.getPersistentDataContainer()
            .set(CUSTOM_ARMOR_KEY, PersistentDataType.INTEGER, armorUpgrade.getLevel());
        itemStack.setItemMeta(itemMeta);
      }
    }

    flameConfigService.save(ArmorUpgradeConfig.class);

  }


  @Execute(name = "removeLevel")
  void removeLevel(@Context final CommandSender sender, @Arg final int level) {
    armorUpgradeService.removeLevel(level);
    flameConfigService.save(ArmorUpgradeConfig.class);
  }

  @Execute(name = "get")
  void getArmorUpgradesByLevel(@Context final Player sender, @Arg final int level) {
    final List<ArmorUpgrade> armorUpgrades = armorUpgradeService.findByLevel(level);
    if (armorUpgrades == null || armorUpgrades.isEmpty()) {
      BukkitMessage.from("&cNie znaleziono żadnych ulepszeń dla tego typu zbroi.").deliver(sender);
      return;
    }

    final List<ItemStack> itemStacks = armorUpgrades.stream().map(ArmorUpgrade::getItemStack)
        .toList();
    InventoryUtil.addItems(sender, itemStacks);
  }

}

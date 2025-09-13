package io.github.flamehub.armor.upgrade;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

@Command(name = "ulepsz", aliases = {"ulepszator", "ulepszzbroje", "uzbrojenie"})
public final class ArmorUpgradeCommand {

  private final ArmorUpgradeService armorUpgradeService;
  private final ArmorUpgradeConfig armorUpgradeConfig;
  private final Economy economy;

  public ArmorUpgradeCommand(
      final ArmorUpgradeService armorUpgradeService,
      final ArmorUpgradeConfig armorUpgradeConfig,
      final Economy economy) {
    this.armorUpgradeService = armorUpgradeService;
    this.armorUpgradeConfig = armorUpgradeConfig;
    this.economy = economy;
  }

  @Execute
  void exec(@Context final Player player) {
    final ArmorUpgradeGui armorUpgradeGui = new ArmorUpgradeGui(armorUpgradeService, armorUpgradeConfig, economy);
    armorUpgradeGui.open(player);
  }
}

package io.github.flamehub.flamebox;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.util.InventoryUtil;
import io.github.flamehub.commons.bukkit.util.TitleUtil;
import io.github.flamehub.commons.config.FlameConfigService;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Command(name = "flameboxadmin")
@Permission("server.commands.flameboxadmin")
public final class FlameBoxCommand {

  private final FlameConfigService flameConfigService;
  private final FlameBoxConfig flameBoxConfig;

  public FlameBoxCommand(
      final FlameConfigService flameConfigService,
      final FlameBoxConfig flameBoxConfig
  ) {
    this.flameConfigService = flameConfigService;
    this.flameBoxConfig = flameBoxConfig;
  }

  @Execute(name = "giveall")
  public void giveAll(@Context final CommandSender sender, @Arg final int amount) {
    if (sender instanceof Player) {
      return;
    }

    final ItemStack item = flameBoxConfig.getFlameBoxItem();
    if (item == null) {
      sender.sendMessage("item == null");
      return;
    }

    final ItemStack clone = item.clone();
    clone.setAmount(amount);

    for (final Player player : sender.getServer().getOnlinePlayers()) {
      InventoryUtil.addItem(player, clone);
      TitleUtil.title(player, " ", "&7Cały serwer otrzymał: &f&lx" + amount
              + " &#C40505&lғ&#D40707&lʟ&#E40909&lᴀ&#F40B0B&lᴍ&#F40B0B&lᴇ&#E40909&lʙ&#D40707&lᴏ&#C40505&lx",
          0, 20, 0);
    }

  }

  @Execute(name = "getflamebox")
  void get(@Context final Player player) {
    final ItemStack item = flameBoxConfig.getFlameBoxItem();
    if (item == null) {
      player.sendMessage("item == null");
      return;
    }

    InventoryUtil.addItem(player, item.clone());
  }

  @Execute(name = "give")
  void give(@Context final CommandSender sender, @Arg final Player target, @Arg final int amount) {
    final ItemStack item = flameBoxConfig.getFlameBoxItem();
    if (item == null) {
      sender.sendMessage("item == null");
      return;
    }

    final ItemStack clone = item.clone();
    clone.setAmount(amount);
    InventoryUtil.addItem(target.getPlayer(), clone);
  }

  @Execute(name = "setdropitem")
  void addDropItem(@Context final Player player, @Arg final int slot, @Arg final double chance) {

    final ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
    if (itemInMainHand.getType().isAir()) {
      return;
    }

    flameBoxConfig.getDrops().put(slot, new FlameBoxDrop(itemInMainHand.clone(), chance));
    flameConfigService.save(FlameBoxConfig.class);

  }

  @Execute(name = "removeitem")
  void removeDropItem(@Context final Player player, @Arg final int slot) {

    flameBoxConfig.getDrops().remove(slot);
    flameConfigService.save(FlameBoxConfig.class);

  }

  @Execute(name = "setpreviewloc")
  void setScratchPreviewLoc(@Context final Player player) {
    final Block targetBlock = player.getTargetBlockExact(5);
    if (targetBlock == null) {
      return;
    }

    flameBoxConfig.getPreviewLocation().add(targetBlock.getLocation());
    flameConfigService.save(FlameBoxConfig.class);
  }

  @Execute(name = "removepreviewloc")
  void removeScratchPreviewLoc(@Context final Player player) {
    final Block targetBlock = player.getTargetBlockExact(5);
    if (targetBlock == null) {
      return;
    }

    flameBoxConfig.getPreviewLocation().remove(targetBlock.getLocation());
    flameConfigService.save(FlameBoxConfig.class);
  }


}

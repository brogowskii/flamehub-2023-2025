package io.github.flamehub.essentials.command;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@Command(name = "repair", aliases = "fix")
@Permission("server.essentials.commands.repair")
final class RepairCommand {

  private final BukkitMessagesService messagesService;

  public RepairCommand(final BukkitMessagesService messagesService) {
    this.messagesService = messagesService;
  }

  @Execute
  void execute(@Context final Player player) {
    final ItemStack itemInHand = player.getInventory().getItemInMainHand();
    if (itemInHand.getType().getMaxDurability() > 0) {
      itemInHand.setDurability((short) 0);
      messagesService.sendMessage(player, "repair.success");
      return;
    }

    messagesService.sendMessage(player, "repair.cant.repair.this.item");
  }

  @Execute(name = "all", aliases = {"a", "*"})
  @Permission("server.essentials.commands.repair.all")
  void all(@Context final Player player) {
    final PlayerInventory inventory = player.getInventory();
    for (final ItemStack content : inventory.getContents()) {
      if (content != null && content.getType() != Material.AIR
          && content.getType().getMaxDurability() > 0) {
        content.setDurability((short) 0);
      }
    }

    for (final ItemStack content : inventory.getArmorContents()) {
      if (content != null && content.getType().getMaxDurability() > 0) {
        content.setDurability((short) 0);
      }
    }

    messagesService.sendMessage(player, "repair.success.all");
  }


}

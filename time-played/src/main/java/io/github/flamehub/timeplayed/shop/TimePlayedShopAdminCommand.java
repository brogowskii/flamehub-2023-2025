package io.github.flamehub.timeplayed.shop;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.config.FlameConfigRefresher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.config.FlameConfigService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Command(name = "timeplayedshopadmin", aliases = {"timeshopadmin"})
@Permission("server.commands.timeplayedshopadmin")
public final class TimePlayedShopAdminCommand extends FlameConfigRefresher {

  private final FlameConfigService flameConfigService;
  private final TimePlayedShopConfig timePlayedShopConfig;

  public TimePlayedShopAdminCommand(FlameConfigService flameConfigService,
      TimePlayedShopConfig timePlayedShopConfig) {
    super(flameConfigService, TimePlayedShopConfig.class);
    this.flameConfigService = flameConfigService;
    this.timePlayedShopConfig = timePlayedShopConfig;
  }


  @Execute(name = "reload")
  void reload(@Context CommandSender commandSender) {
    refreshConfigRemote(commandSender);
  }

  @Execute(name = "setitem")
  void setItem(@Context Player player, @Arg int slot, @Arg int cost) {

    ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
    if (itemInMainHand.getType().isAir()) {
      return;
    }

    this.timePlayedShopConfig.getItemsBySlot()
        .put(slot, new TimePlayedShopItem(itemInMainHand, cost));
    this.flameConfigService.saveLocally(TimePlayedShopConfig.class);
    BukkitMessage.from("&aPomyślnie ustawiono przedmiot!").send(player);
  }

  @Execute(name = "removeitem")
  void removeItem(@Context Player player, @Arg int slot) {

    this.timePlayedShopConfig.getItemsBySlot().remove(slot);
    this.flameConfigService.saveLocally(TimePlayedShopConfig.class);
    BukkitMessage.from("&aPomyślnie usunięto przedmiot!").send(player);
  }

}

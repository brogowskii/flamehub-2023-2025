package io.github.flamehub.essentials.command;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import org.bukkit.entity.Player;

@Command(name = "fly")
@Permission("server.essentials.commands.fly")
final class FlyCommand {

  @Execute
  void execute(@Context final Player player) {
    player.setAllowFlight(!player.getAllowFlight());
  }

  @Execute
  @Permission("server.essentials.commands.fly.other")
  void execute(@Context final Player player, @Arg final Player target) {
    target.setAllowFlight(!target.getAllowFlight());
    BukkitMessage.from("&7Status latania dla gracza &f" + target.getName() + " &7został: &f" + (
            player.getAllowFlight() ? "włączony" : "wyłączony"))
        .deliver(player);
  }

}

package io.github.flamehub.essentials.teleport;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.entity.Player;

@Command(name = "tphere", aliases = "s")
@Permission("server.commands.tphere")
final class TeleportHereCommand {

  @Execute
  public void teleportOther(@Context Player player, @Arg Player target) {
    target.teleport(player.getLocation());
  }

}

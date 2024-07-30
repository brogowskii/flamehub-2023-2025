package io.github.flamehub.essentials.command;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.entity.Player;

@Command(name = "wb", aliases = "workbench")
@Permission("server.essentials.commands.workbench")
final class WorkbenchCommand {

  @Execute
  void execute(@Context final Player player) {
    player.openWorkbench(null, true);
  }

}

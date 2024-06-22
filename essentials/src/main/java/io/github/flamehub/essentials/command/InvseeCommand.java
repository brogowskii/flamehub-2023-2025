package io.github.flamehub.essentials.command;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.entity.Player;

@Command(name = "invsee")
@Permission("server.anarchiaffa.commands.invsee")
final class InvseeCommand {

    @Execute
    void invsee(@Context Player player, @Arg Player target) {

        player.openInventory(target.getInventory());

    }

}

package io.github.flamehub.essentials.command;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.entity.Player;

@Command(name = "enderchest", aliases = "ec")
@Permission("server.essentials.command.enderchest")
final class EnderChestCommand {

    @Execute
    void exec(@Context final Player player) {
        player.openInventory(player.getEnderChest());
    }

    @Execute
    @Permission("server.essentials.command.enderchest.preview")
    void exec(@Context final Player player, @Arg final Player target) {
        player.openInventory(target.getEnderChest());
    }

}

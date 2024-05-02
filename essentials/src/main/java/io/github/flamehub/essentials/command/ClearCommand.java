package io.github.flamehub.essentials.command;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import org.bukkit.entity.Player;

@Command(name = "clear")
@Permission("server.essentials.commands.clear")
final class ClearCommand {

    @Execute
    void execute(@Context final Player player) {

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

    }

    @Execute
    void execute(@Context final Player player, @Arg final Player target) {

        target.getInventory().clear();
        target.getInventory().setArmorContents(null);

        BukkitMessage.from("&aSclearowano eq graczowi: &2" + target.getName()).send(player);

    }

}

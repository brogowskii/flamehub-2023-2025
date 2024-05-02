package io.github.flamehub.essentials.command;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.optional.OptionalArg;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@Command(name = "gamemode", aliases = {"gm"})
@Permission("server.essentials.commands.gamemode")
final class GameModeCommand {

    private final BukkitMessagesService messagesService;

    public GameModeCommand(final BukkitMessagesService messagesService) {
        this.messagesService = messagesService;
    }

    @Execute
    void execute(@Context final Player player, @Arg final GameMode gameMode, @OptionalArg final Player target) {

        if (target == null) {
            player.setGameMode(gameMode);
            this.messagesService.message("gamemode.change.successfuly")
                    .with("game_mode", gameMode.name())
                    .send(player);
            return;
        }

        target.setGameMode(gameMode);
        this.messagesService.message("gamemode.change.successfuly.target")
                .with("game_mode", gameMode.name())
                .with("target", target.getName())
                .send(player);


    }

}

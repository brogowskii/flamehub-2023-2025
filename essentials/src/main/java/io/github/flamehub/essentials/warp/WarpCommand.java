package io.github.flamehub.essentials.warp;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.commons.bukkit.teleport.TeleporterService;
import org.bukkit.entity.Player;

@Command(name = "warp", aliases = {"warps", "warpy"})
final class WarpCommand {

    private final WarpFacade warpFacade;
    private final TeleporterService teleporterService;

    WarpCommand(final WarpFacade warpFacade, final TeleporterService teleporterService) {
        this.warpFacade = warpFacade;
        this.teleporterService = teleporterService;
    }

    @Execute
    void execute(@Context final Player player) {

        WarpGui warpGui = new WarpGui(this.warpFacade, this.teleporterService);
        warpGui.open(player);

    }
}

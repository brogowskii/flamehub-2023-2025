package io.github.flamehub.scratch;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;

@Command(name = "scratch", aliases = {"zdrapka", "zdrapki"})
public final class ScratchCommand {

    private final ScratchConfig scratchConfig;

    public ScratchCommand(ScratchConfig scratchConfig) {
        this.scratchConfig = scratchConfig;
    }

    @Execute
    void preview(@Context Player player) {
        new ScratchDropGui(player, scratchConfig).openPreview();
    }
}

package io.github.flamehub.commons.bukkit.automessage;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "automessage")
@Permission("server.commands.automessage")
public final class AutoMessageReloadCommand {

    private final AutoMessageConfig autoMessageConfig;

    public AutoMessageReloadCommand(AutoMessageConfig autoMessageConfig) {
        this.autoMessageConfig = autoMessageConfig;
    }

    @Execute
    void execute(@Context CommandSender sender) {
        this.autoMessageConfig.load();
    }

}

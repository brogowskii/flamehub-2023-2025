package io.github.flamehub.proxy.core.motd;

import com.velocitypowered.api.command.CommandSource;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.proxy.core.text.TextUtil;

@Command(name = "motd", aliases = "motdreload")
@Permission("server.velocity.commands.motd")
public final class MotdCommand {

    private final MotdConfig motdConfig;

    public MotdCommand(MotdConfig motdConfig) {
        this.motdConfig = motdConfig;
    }

    @Execute
    void execute(@Context CommandSource commandSource) {
        this.motdConfig.load();
        commandSource.sendMessage(TextUtil.parse("&aSuccessfully reloaded motd configuration."));
    }

}

package io.github.flamehub.commons.bukkit.execute;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.messenger.RedisMessenger;
import org.bukkit.command.CommandSender;

@Command(name = "execute", aliases = {"commandexec", "perform"})
@Permission("server.commands.perform")
public final class ExecuteCommand {

    private final RedisMessenger redisMessenger;

    public ExecuteCommand(RedisMessenger redisMessenger) {
        this.redisMessenger = redisMessenger;
    }

    @Execute
    void execute(@Context CommandSender sender, @Arg String serverCategory, @Join String command) {

        this.redisMessenger.publish(serverCategory, new ExecutePacket(command));
        sender.sendMessage(TextUtil.parse("&aPomyślnie wykonano komende &7" + command + " &ana każdym serwerze w kategorii &7" + serverCategory));

    }

}

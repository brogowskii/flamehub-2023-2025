package io.github.flamehub.essentials.command;

import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitSettings;
import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public final class CommandConfigurator {
    public CommandConfigurator(
            final LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> liteCommandsBuilder,
            final BukkitMessagesService messagesService
    ) {
        liteCommandsBuilder.commands(LiteCommandsAnnotations.of(
                new ClearCommand(),
                new EnderChestCommand(),
                new FeedCommand(messagesService),
                new FlyCommand(),
                new GameModeCommand(messagesService),
                new HealCommand(messagesService),
                new RepairCommand(messagesService),
                new SpeedCommand(messagesService),
                new WorkbenchCommand(),
                new InvseeCommand(),
                new GammaCommand()
        ));

    }
}

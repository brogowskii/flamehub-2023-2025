package io.github.flamehub.essentials.command;

import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import org.bukkit.plugin.Plugin;

public final class CommandModule extends BukkitModule {
    public CommandModule(Plugin plugin, FlameDispatcher flameDispatcher) {
        super(plugin, flameDispatcher);

        addCommands(
                new ClearCommand(),
                new EnderChestCommand(),
                new FeedCommand(super.messagesService),
                new FlyCommand(),
                new GameModeCommand(super.messagesService),
                new HealCommand(super.messagesService),
                new RepairCommand(super.messagesService),
                new SpeedCommand(super.messagesService),
                new WorkbenchCommand()
        );

    }
}

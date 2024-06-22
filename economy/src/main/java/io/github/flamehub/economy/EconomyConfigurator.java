package io.github.flamehub.economy;

import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitSettings;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.economy.user.EconomyUserFacade;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

public final class EconomyConfigurator {

    public EconomyFacade economyFacade(
            final LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> liteCommandsBuilder,
            final Plugin plugin,
            final FlameDispatcher flameDispatcher,
            final EconomyUserFacade economyUserFacade,
            final BukkitMessagesService messagesService,
            final NetworkMessageService networkMessageService
    ) {

        final EconomyFacade economyFacade = new EconomyFacade(economyUserFacade);
        final ServicesManager servicesManager = plugin.getServer().getServicesManager();
        servicesManager.register(Economy.class, new EconomyVaultProvider(economyUserFacade), plugin, ServicePriority.Normal);

        new EconomyPlaceholder(economyUserFacade).register();

        liteCommandsBuilder.commands(LiteCommandsAnnotations.of(
                new EconomyCommand(flameDispatcher, messagesService, economyUserFacade),
                new PayCommand(economyUserFacade, messagesService, networkMessageService),
                new BalanceCommand(messagesService)
        ));

        return economyFacade;
    }

}
package io.github.flamehub.economy;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.util.NumberConverter;
import io.github.flamehub.economy.user.*;
import org.bukkit.command.CommandSender;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;

import java.math.BigDecimal;

@Command(name = "economy", aliases = {"eco"})
@Permission("server.commands.economy")
final class EconomyCommand {

    private final FlameDispatcher flameDispatcher;
    private final BukkitMessagesService messagesService;
    private final EconomyUserFacade economyUserFacade;

    EconomyCommand(
            FlameDispatcher flameDispatcher, final BukkitMessagesService messagesService,
            final EconomyUserFacade economyUserFacade
    ) {
        this.flameDispatcher = flameDispatcher;
        this.messagesService = messagesService;
        this.economyUserFacade = economyUserFacade;
    }

    @Execute(name = "balance")
    void balance(@Context final CommandSender sender, @Async @Arg final EconomyUser economyUser) {
        BukkitMessage
                .from("&eStan konta tego gracza wynosi &6$" + NumberConverter.convertNumber(economyUser.getMoney().doubleValue()))
                .send(sender);
    }

    @Execute(name = "add")
    void add(
            @Context final CommandSender sender,
            @Async @Arg final EconomyUser economyUser,
            @Arg final double value
    ) {

        economyUser.addMoney(value);
        this.economyUserFacade.update(economyUser, value, EconomyUserUpdateType.ADD);

        BukkitMessage.from("&aDodano &e" + value + " &ado konta gracza &e" + economyUser.getName()).send(sender);

    }

    @Execute(name = "remove")
    void remove(
            @Context final CommandSender sender,
            @Async @Arg final EconomyUser economyUser,
            @Arg final double value
    ) {

        economyUser.removeMoney(value);
        this.economyUserFacade.update(economyUser, value, EconomyUserUpdateType.REMOVE);

        BukkitMessage.from("&aOdebrano &e" + value + " &ado konta gracza &e" + economyUser.getName()).send(sender);

    }

    @Execute(name = "set")
    void set(
            @Context final CommandSender sender,
            @Async @Arg final EconomyUser economyUser,
            @Arg final double value
    ) {

        economyUser.setMoney(BigDecimal.valueOf(value));
        this.economyUserFacade.update(economyUser, value, EconomyUserUpdateType.SET);

        BukkitMessage.from("&aUstawiono &e" + value + " &ado konta gracza &e" + economyUser.getName()).send(sender);

    }
}

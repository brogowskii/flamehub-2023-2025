package io.github.flamehub.economy;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.util.NumberConverter;
import io.github.flamehub.economy.user.EconomyUserRepository;
import io.github.flamehub.economy.user.updater.EconomyUserUpdate;
import org.bukkit.command.CommandSender;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.economy.user.EconomyUser;
import io.github.flamehub.economy.user.EconomyUserCache;
import io.github.flamehub.economy.user.updater.EconomyUserUpdater;

import java.math.BigDecimal;

@Command(name = "economy", aliases = {"eco"})
@Permission("server.commands.economy")
public final class EconomyCommand {

    private final BukkitMessagesService messagesService;
    private final EconomyUserCache economyUserCache;
    private final EconomyUserUpdater economyUserUpdater;
    private final EconomyUserRepository economyUserRepository;

    public EconomyCommand(BukkitMessagesService messagesService, EconomyUserCache economyUserCache, EconomyUserUpdater economyUserUpdater, EconomyUserRepository economyUserRepository) {
        this.messagesService = messagesService;
        this.economyUserCache = economyUserCache;
        this.economyUserUpdater = economyUserUpdater;
        this.economyUserRepository = economyUserRepository;
    }

    @Execute(name = "balance")
    void balance(@Context CommandSender sender, @Arg String playerName) {
        EconomyUser economyUser = this.economyUserCache.findByName(playerName);
        if (economyUser == null) {
            this.messagesService.sendMessage(sender, "user.does.not.exist");
            return;
        }

        BukkitMessage.from("&eStan konta tego gracza wynosi &6" + NumberConverter.convertNumber(economyUser.getMoney().doubleValue()))
                .send(sender);
    }

    @Execute(name = "add")
    void add(@Context CommandSender sender, @Arg String playerName, @Arg double value) {

        EconomyUser economyUser = this.economyUserCache.findByName(playerName);
        if (economyUser == null) {
            this.messagesService.sendMessage(sender, "user.does.not.exist");
            return;
        }

        economyUser.addMoney(value);
        this.economyUserUpdater.update(economyUser, new EconomyUserUpdate(economyUser.getUniqueId(), economyUser.getMoney().doubleValue()));

        BukkitMessage.from("&aDodano &e" + value + " &ado konta gracza &e" + playerName).send(sender);

    }

    @Execute(name = "remove")
    void remove(@Context CommandSender sender, @Arg String playerName, @Arg double value) {

        EconomyUser economyUser = this.economyUserCache.findByName(playerName);
        if (economyUser == null) {
            this.messagesService.sendMessage(sender, "user.does.not.exist");
            return;
        }

        economyUser.removeMoney(value);
        this.economyUserUpdater.update(economyUser, new EconomyUserUpdate(economyUser.getUniqueId(), economyUser.getMoney().doubleValue()));

        BukkitMessage.from("&aOdebrano &e" + value + " &ado konta gracza &e" + playerName).send(sender);

    }

    @Execute(name = "set")
    void set(@Context CommandSender sender, @Arg String playerName, @Arg double value) {

        EconomyUser economyUser = this.economyUserCache.findByName(playerName);
        if (economyUser == null) {
            this.messagesService.sendMessage(sender, "user.does.not.exist");
            return;
        }

        economyUser.setMoney(BigDecimal.valueOf(value));
        this.economyUserUpdater.update(economyUser, new EconomyUserUpdate(economyUser.getUniqueId(), economyUser.getMoney().doubleValue()));

        BukkitMessage.from("&aUstawiono &e" + value + " &ado konta gracza &e" + playerName).send(sender);

    }
}

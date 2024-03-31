package io.github.flamehub.economy;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.util.NumberConverter;
import io.github.flamehub.economy.user.EconomyUser;
import io.github.flamehub.economy.user.EconomyUserCache;

@Command(name = "balance", aliases = {"bal", "money", "pieniadze", "stankonta"})
public final class BalanceCommand {

    private final EconomyUserCache economyUserCache;
    private final BukkitMessagesService messagesService;

    public BalanceCommand(EconomyUserCache economyUserCache, BukkitMessagesService messagesService) {
        this.economyUserCache = economyUserCache;
        this.messagesService = messagesService;
    }

    @Execute
    void execute(@Context Player player) {

        EconomyUser economyUser = this.economyUserCache.findByUniqueId(player.getUniqueId());
        this.messagesService.getAsText("economy.account.balance")
                .placeholder("{FORMATTED_MONEY}", NumberConverter.convertNumber(economyUser.getMoney().doubleValue()))
                .send(player);


    }

}

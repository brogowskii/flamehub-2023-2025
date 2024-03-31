package io.github.flamehub.economy;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.commons.bukkit.CommonsPlugin;
import io.github.flamehub.commons.bukkit.util.NumberConverter;
import io.github.flamehub.commons.network.message.NetworkMessageFilterBuilder;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import org.bukkit.entity.Player;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.util.RoundUtil;
import io.github.flamehub.economy.user.EconomyUser;
import io.github.flamehub.economy.user.EconomyUserCache;

@Command(name = "pay", aliases = {"przelej", "przelew", "przelejpieniadze"})
public final class PayCommand {

    private final BukkitMessagesService messagesService;
    private final EconomyUserCache economyUserCache;
    private final NetworkMessageService networkMessageService;

    public PayCommand(BukkitMessagesService messagesService, EconomyUserCache economyUserCache, NetworkMessageService networkMessageService) {
        this.messagesService = messagesService;
        this.economyUserCache = economyUserCache;
        this.networkMessageService = networkMessageService;
    }

    @Execute
    void execute(@Context Player player, @Arg Player target, @Arg double value) {

        if (player.getUniqueId().equals(target.getUniqueId())) {
            return;
        }

        EconomyUser economyUser = this.economyUserCache.findByUniqueId(player.getUniqueId());
        EconomyUser targetEconomyUser = this.economyUserCache.findByUniqueId(target.getUniqueId());

        if (targetEconomyUser == null) {
            this.messagesService.sendMessage(player, "user.does.not.exist");
            return;
        }

        if (economyUser.getMoney().doubleValue() < value || value <= 0) {
            this.messagesService.sendMessage(player, "economy.not.enough.money");
            return;
        }

        economyUser.removeMoney(value);
        economyUser.setNeedUpdate(true);
        targetEconomyUser.addMoney(value);
        targetEconomyUser.setNeedUpdate(true);

        this.networkMessageService.send(
                "&8[&6&lPRZELEWY&8] &7Gracz &f" + player.getName() + " &7przelał graczowi &f" + target.getName() + " &7kwote o wysokości: &e$" + NumberConverter.convertNumber(value),
                new NetworkMessageFilterBuilder()
                        .targetServerCategory(CommonsPlugin.getInstance().getNetworkServerCache().getCurrent().getCategory())
                        .targetPermission("server.eco.logs")
                        .build(),
                NetworkMessageType.CHAT
        );

        this.messagesService.getAsText("economy.pay")
                .placeholder("{PLAYER}", target.getName())
                .placeholder("{VALUE}", RoundUtil.round(value, 2))
                .send(player);

        this.messagesService.getAsText("economy.pay.received")
                .placeholder("{PLAYER}", player.getName())
                .placeholder("{VALUE}", RoundUtil.round(value, 2))
                .send(target);

    }

}

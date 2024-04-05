package io.github.flamehub.wallet;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import io.github.flamehub.commons.util.RoundUtil;
import io.github.flamehub.wallet.api.WalletUser;
import io.github.flamehub.wallet.item.WalletOfferConfig;
import io.github.flamehub.wallet.user.update.WalletUserUpdate;
import io.github.flamehub.wallet.user.update.WalletUserUpdater;
import org.bukkit.command.CommandSender;

import java.math.BigDecimal;

@Permission("server.recode.awallet")
@Command(name = "awallet", aliases = {"aw", "ais"})
public final class WalletAdminCommand {

    private final NetworkMessageService networkMessageService;
    private final BukkitMessagesService messageService;
    private final WalletOfferConfig walletOfferConfig;
    private final WalletUserUpdater walletUserUpdater;

    public WalletAdminCommand(
            NetworkMessageService networkMessageService,
            BukkitMessagesService messageService,
            WalletOfferConfig walletOfferConfig,
            WalletUserUpdater walletUserUpdater
    ) {
        this.networkMessageService = networkMessageService;
        this.messageService = messageService;
        this.walletOfferConfig = walletOfferConfig;
        this.walletUserUpdater = walletUserUpdater;
    }

    @Execute(name = "reload")
    void reload(@Context CommandSender sender) {
        this.walletOfferConfig.load();
        sender.sendMessage(TextUtil.parse("&aSuccessfully reloaded wallet offers config."));
    }

    @Execute(name = "add")
    void add(@Context CommandSender sender, @Async @Arg WalletUser walletUser, @Arg("money") double money) {

        walletUser.addMoney(BigDecimal.valueOf(money));
        this.walletUserUpdater.update(walletUser, new WalletUserUpdate(walletUser.getUniqueId(), walletUser.getMoney().doubleValue()));

        BukkitMessage.from("&aSuccessfully added &7" + money + " &ato &7" + walletUser.getName() + "&a.").send(sender);

    }

    @Execute(name = "remove")
    void remove(@Context CommandSender sender, @Async @Arg WalletUser walletUser, @Arg double money) {

        walletUser.subtractMoney(BigDecimal.valueOf(money));
        this.walletUserUpdater.update(
                walletUser,
                new WalletUserUpdate(walletUser.getUniqueId(), walletUser.getMoney().doubleValue())
        );

        BukkitMessage.from("&aSuccessfully removed &7" + money + " &afrom &7" + walletUser.getName() + "&a.").send(sender);
    }

    @Execute(name = "set")
    void set(@Context CommandSender sender, @Async @Arg WalletUser walletUser, @Arg double money) {

        walletUser.setMoney(BigDecimal.valueOf(money));
        this.walletUserUpdater.update(walletUser, new WalletUserUpdate(walletUser.getUniqueId(), walletUser.getMoney().doubleValue()));

        BukkitMessage.from("&aSuccessfully set money for &7" + walletUser.getName() + " &ato &7" + money + "&a.")
                .send(sender);
    }

    @Execute(name = "addWithBroadcast")
    void addWithBroadcast(
            @Context CommandSender sender,
            @Async @Arg WalletUser walletUser,
            @Arg double money
    ) {

        add(sender, walletUser, money);
        this.networkMessageService.send(
                this.messageService.getAsText("wallet.charge.vpln.broadcast")
                        .placeholder("{PLAYER}", walletUser.getName())
                        .placeholder("{MONEY}", RoundUtil.round(money, 2))
                        .build(),
                NetworkMessageType.CHAT
        );

    }

    @Execute(name = "accountBalance", aliases = "balance")
    void accountBalance(@Context CommandSender sender, @Async @Arg WalletUser walletUser) {
        BukkitMessage.from("&eStan konta tego gracza wynosi: &6" + walletUser.getMoney().doubleValue()).send(sender);

    }

}

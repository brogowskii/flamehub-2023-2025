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
import io.github.flamehub.commons.util.DiscordWebhook;
import io.github.flamehub.commons.util.RoundUtil;
import io.github.flamehub.commons.util.TimeUtil;
import io.github.flamehub.wallet.api.WalletUserMoneyChangeType;
import io.github.flamehub.wallet.api.WalletUser;
import io.github.flamehub.wallet.item.WalletOfferConfig;
import io.github.flamehub.wallet.log.WalletLog;
import io.github.flamehub.wallet.log.WalletLogAction;
import io.github.flamehub.wallet.log.WalletLogRepository;
import io.github.flamehub.wallet.user.WalletUserUpdater;
import org.bukkit.command.CommandSender;

import java.awt.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Permission("server.recode.awallet")
@Command(name = "awallet", aliases = {"aw", "ais"})
public final class WalletAdminCommand {

    private final static String WEB_HOOK_URL = "https://discord.com/api/webhooks/1227313255941148722/XhslfiGfsANTpK4APpmPwaoWxdhUCM889nR6Rq_J5wwcSbzkpetO2tsTEcOZboRS_gix";

    private final NetworkMessageService networkMessageService;
    private final BukkitMessagesService messageService;
    private final WalletOfferConfig walletOfferConfig;
    private final WalletUserUpdater walletUserUpdater;
    private final WalletLogRepository walletLogRepository;

    public WalletAdminCommand(
            NetworkMessageService networkMessageService,
            BukkitMessagesService messageService,
            WalletOfferConfig walletOfferConfig,
            WalletUserUpdater walletUserUpdater,
            WalletLogRepository walletLogRepository) {
        this.networkMessageService = networkMessageService;
        this.messageService = messageService;
        this.walletOfferConfig = walletOfferConfig;
        this.walletUserUpdater = walletUserUpdater;
        this.walletLogRepository = walletLogRepository;
    }

    @Execute(name = "reload")
    void reload(@Context CommandSender sender) {
        this.walletOfferConfig.load();
        sender.sendMessage(TextUtil.parse("&aSuccessfully reloaded wallet offers config."));
    }

    @Async
    @Execute(name = "add")
    void add(@Context CommandSender sender, @Async @Arg WalletUser walletUser, @Arg("money") double money) {

        walletUser.addMoney(BigDecimal.valueOf(money));
        this.walletUserUpdater.update(walletUser, money, WalletUserMoneyChangeType.ADD);

        WalletLog walletLog = new WalletLog(WalletLogAction.ADD_MONEY);
        walletLog.setAdminName(sender.getName());
        walletLog.setAmount(money);
        walletLog.setBuyerName(walletUser.getName());
        this.walletLogRepository.save(walletLog);

        DiscordWebhook discordWebhook = new DiscordWebhook(WEB_HOOK_URL);
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
        embed.setAuthor("Doładowania || Flamehub.pl", null, "https://i.imgur.com/B3lRUdp.png");
        embed.setColor(Color.YELLOW);
        embed.addField("**GOAT:**", "<@565502041909362710>", true);
        embed.addField("**Akcja:**", "Doładowanie konta", true);
        embed.addField("**Administrator:**", sender.getName(), true);
        embed.addField("**Komu:**", walletUser.getName(), true);
        embed.addField("**Ile:**", String.valueOf(money), true);
        embed.setImage("https://minotar.net/helm/" + walletUser.getName() + "/100.png");
        embed.setTimestamp(Instant.now().toString());
        embed.setFooter("FlameHub.pl • " + TimeUtil.formatDate(Instant.now()), "https://i.imgur.com/B3lRUdp.png");
        discordWebhook.addEmbed(embed);
        discordWebhook.execute();

        BukkitMessage.from("&aSuccessfully added &7" + money + " &ato &7" + walletUser.getName() + "&a.").send(sender);

    }

    @Async
    @Execute(name = "remove")
    void remove(@Context CommandSender sender, @Async @Arg WalletUser walletUser, @Arg double money) {

        walletUser.subtractMoney(BigDecimal.valueOf(money));
        this.walletUserUpdater.update(walletUser, money, WalletUserMoneyChangeType.REMOVE);

        WalletLog walletLog = new WalletLog(WalletLogAction.REMOVE_MONEY);
        walletLog.setAdminName(sender.getName());
        walletLog.setAmount(money);
        walletLog.setBuyerName(walletUser.getName());
        this.walletLogRepository.save(walletLog);

        DiscordWebhook discordWebhook = new DiscordWebhook(WEB_HOOK_URL);
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
        embed.setAuthor("Doładowania || Flamehub.pl", null, "https://i.imgur.com/B3lRUdp.png");
        embed.setColor(Color.YELLOW);
        embed.addField("**GOAT:**", "<@565502041909362710>", true);
        embed.addField("**Akcja:**", "Usunięcie vpln", true);
        embed.addField("**Administrator:**", sender.getName(), true);
        embed.addField("**Komu:**", walletUser.getName(), true);
        embed.addField("**Ile:**", String.valueOf(money), true);
        embed.setImage("https://minotar.net/helm/" + walletUser.getName() + "/100.png");
        embed.setTimestamp(Instant.now().toString());
        embed.setFooter("FlameHub.pl • " + TimeUtil.formatDate(Instant.now()), "https://i.imgur.com/B3lRUdp.png");
        discordWebhook.addEmbed(embed);
        discordWebhook.execute();

        BukkitMessage.from("&aSuccessfully removed &7" + money + " &afrom &7" + walletUser.getName() + "&a.").send(sender);
    }

    @Async
    @Execute(name = "set")
    void set(@Context CommandSender sender, @Async @Arg WalletUser walletUser, @Arg double money) {

        walletUser.setMoney(BigDecimal.valueOf(money));
        this.walletUserUpdater.update(walletUser, money, WalletUserMoneyChangeType.SET);

        WalletLog walletLog = new WalletLog(WalletLogAction.SET_MONEY);
        walletLog.setAdminName(sender.getName());
        walletLog.setAmount(money);
        walletLog.setBuyerName(walletUser.getName());
        this.walletLogRepository.save(walletLog);

        DiscordWebhook discordWebhook = new DiscordWebhook(WEB_HOOK_URL);
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
        embed.setAuthor("Doładowania || Flamehub.pl", null, "https://i.imgur.com/B3lRUdp.png");
        embed.setColor(Color.YELLOW);
        embed.addField("**GOAT:**", "<@565502041909362710>", true);
        embed.addField("**Akcja:**", "Ustawienie ilosci vpln", true);
        embed.addField("**Administrator:**", sender.getName(), true);
        embed.addField("**Komu:**", walletUser.getName(), true);
        embed.addField("**Ile:**", String.valueOf(money), true);
        embed.setImage("https://minotar.net/helm/" + walletUser.getName() + "/100.png");
        embed.setTimestamp(Instant.now().toString());
        embed.setFooter("FlameHub.pl • " + TimeUtil.formatDate(Instant.now()), "https://i.imgur.com/B3lRUdp.png");
        discordWebhook.addEmbed(embed);
        discordWebhook.execute();

        BukkitMessage.from("&aSuccessfully set money for &7" + walletUser.getName() + " &ato &7" + money + "&a.")
                .send(sender);
    }

    @Async
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

    @Async
    @Execute(name = "logs", aliases = {"history"})
    void logs(@Context CommandSender sender, @Arg WalletLogAction action, @Arg String who) {

        List<WalletLog> load = this.walletLogRepository.load(who, action);
        if (load.isEmpty()) {
            BukkitMessage.from("&cBrak historii dla &7" + who + "&c.").send(sender);
            return;
        }

        for (WalletLog walletLog : load) {

            BukkitMessage.from(
                    "",
                    "&8- &f" + action.name() + " &7" + TimeUtil.formatDate(walletLog.getDate())
            ).send(sender);
            if (action == WalletLogAction.BUY) {
                BukkitMessage.from("&8- &a" + walletLog.getBuyerName() + " &7kupił &a" + walletLog.getBoughtItem() + " &7za &a" + walletLog.getAmount()).send(sender);
            } else if (action == WalletLogAction.ADD_MONEY) {
                BukkitMessage.from("&8- &a" + walletLog.getAdminName() + " &7dodał &a" + walletLog.getAmount() + " &7graczowi &a" + walletLog.getBuyerName()).send(sender);
            }
            else if (action == WalletLogAction.REMOVE_MONEY) {
                BukkitMessage.from("&8- &a" + walletLog.getAdminName() + " &7zabrał &a" + walletLog.getAmount() + " &7graczowi &a" + walletLog.getBuyerName()).send(sender);
            }
            else if (action == WalletLogAction.SET_MONEY) {
                BukkitMessage.from("&8- &a" + walletLog.getAdminName() + " &7ustawił stan konta &a" + walletLog.getBuyerName() + " &7na &a" + walletLog.getAmount()).send(sender);
            }

        }

    }

}

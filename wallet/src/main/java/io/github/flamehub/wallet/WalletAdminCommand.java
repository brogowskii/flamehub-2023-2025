package io.github.flamehub.wallet;

import static io.github.flamehub.commons.util.CompletableFutures.NIL;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.flag.Flag;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.config.FlameConfigRefresherCommand;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.config.FlameConfigService;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import io.github.flamehub.commons.util.DiscordWebhook;
import io.github.flamehub.commons.util.RoundUtil;
import io.github.flamehub.commons.util.TimeUtil;
import io.github.flamehub.wallet.item.WalletOfferConfig;
import io.github.flamehub.wallet.log.WalletLog;
import io.github.flamehub.wallet.log.WalletLogAction;
import io.github.flamehub.wallet.log.WalletLogBuilder;
import io.github.flamehub.wallet.log.WalletLogGui;
import io.github.flamehub.wallet.log.WalletLogRepository;
import io.github.flamehub.wallet.user.WalletUserFacade;
import java.awt.Color;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

@Permission("server.recode.awallet")
@Command(name = "awallet", aliases = {"aw", "ais"})
final class WalletAdminCommand extends FlameConfigRefresherCommand {

  private final static String WEB_HOOK_URL = "https://discord.com/api/webhooks/1283431416708858000/kxHQg9w2mH92uWngxPhAUHtywflZZALo9B-GkNhg-qukZcSuFuXqHy14joS4gVyhTwAI";

  private final FlameDispatcher flameDispatcher;
  private final NetworkMessageService networkMessageService;
  private final BukkitMessagesService messageService;

  private final WalletUserFacade walletUserFacade;
  private final WalletLogRepository walletLogRepository;

  WalletAdminCommand(
      final NetworkMessageService networkMessageService,
      final BukkitMessagesService messageService,
      final FlameConfigService flameConfigService,
      final FlameDispatcher flameDispatcher,
      final WalletUserFacade walletUserFacade,
      final WalletLogRepository walletLogRepository) {
    super(flameConfigService, WalletOfferConfig.class);
    this.networkMessageService = networkMessageService;
    this.messageService = messageService;
    this.flameDispatcher = flameDispatcher;
    this.walletUserFacade = walletUserFacade;
    this.walletLogRepository = walletLogRepository;
  }

  @Execute(name = "add")
  CompletableFuture<Void> add(
      final @Context CommandSender sender,
      final @Arg("networkPlayer") String name,
      final @Arg double money,
      final @Flag("-b") boolean broadcast) {

    if (money <= 0) {
      BukkitMessage.from("&cWartośc pieniędzy musi być dodatnia").deliver(sender);
      return NIL;
    }

    return supplyAsync(() -> walletUserFacade.findByName(name))
        .thenCompose(context -> {
          if (context == null) {
            BukkitMessage.from("&cNie znaleziono podanego gracza w bazie danych").deliver(sender);
            return NIL;
          }

          return walletUserFacade
              .mutate(context.getUniqueId(), mutator -> mutator.addMoney(BigDecimal.valueOf(money)))
              .thenRun(() -> {

                BukkitMessage.from(
                    "&aPomyślnie dodano &2%s &adla &2%s&a.".formatted(money, context.getName())
                ).deliver(sender);

                if (broadcast) {
                  networkMessageService.send(
                      messageService.getAsText("wallet.charge.vpln.broadcast")
                          .placeholder("{PLAYER}", context.getName())
                          .placeholder("{MONEY}", RoundUtil.round(money, 2))
                          .build(),
                      NetworkMessageType.CHAT
                  );
                }

                final WalletLog walletLog = WalletLogBuilder.create()
                    .action(WalletLogAction.ADD_MONEY)
                    .adminName(sender.getName())
                    .buyerName(context.getName())
                    .amount(money)
                    .build();
                walletLogRepository.save(walletLog);
                sendWebHook(WalletLogAction.ADD_MONEY, context.getName(), sender, money);

              });
        })
        .exceptionally(ex -> {
          BukkitMessage.from("&cWystąpił błąd: " + ex.getMessage()).deliver(sender);
          ex.printStackTrace();
          return null;
        });

  }

  @Execute(name = "remove")
  CompletableFuture<Void> remove(
      final @Context CommandSender sender,
      final @Arg("networkPlayer") String name,
      final @Arg double money) {

    if (money <= 0) {
      BukkitMessage.from("&cWartośc pieniędzy musi być dodatnia").deliver(sender);
      return NIL;
    }

    return supplyAsync(() -> walletUserFacade.findByName(name))
        .thenCompose(context -> {

          if (context == null) {
            BukkitMessage.from("&cNie znaleziono podanego gracza w bazie danych").deliver(sender);
            return NIL;
          }

          return walletUserFacade
              .mutate(context.getUniqueId(), mutator -> mutator.subtractMoney(BigDecimal.valueOf(money)))
              .thenRun(() -> {
                BukkitMessage.from(
                    "&aUsunięto &2%s &az konta gracza %s".formatted(money, context.getName()))
                    .deliver(sender);

                final WalletLog walletLog = WalletLogBuilder.create()
                    .action(WalletLogAction.REMOVE_MONEY)
                    .adminName(sender.getName())
                    .buyerName(context.getName())
                    .amount(money)
                    .build();
                walletLogRepository.save(walletLog);

                sendWebHook(WalletLogAction.REMOVE_MONEY, context.getName(), sender, money);

              });

        });
  }

  @Execute(name = "set")
  CompletableFuture<Void> set(
      final @Context CommandSender sender,
      final @Arg("networkPlayer") String name,
      final @Arg double money) {

    if (money < 0) {
      BukkitMessage.from("&cWartośc nie może byc ujemna").deliver(sender);
      return NIL;
    }

    return supplyAsync(() -> walletUserFacade.findByName(name))
        .thenCompose(context -> {

          if (context == null) {
            BukkitMessage.from("&cNie znaleziono podanego gracza w bazie danych").deliver(sender);
            return NIL;
          }

          return walletUserFacade
              .mutate(context.getUniqueId(), mutator -> mutator.setMoney(BigDecimal.valueOf(money)))
              .thenRun(() -> {
                BukkitMessage.from(
                    "&aUstawiono stan konta dla gracza &2%s &ana &2%s".formatted(context.getName(),
                        money))
                    .deliver(sender);

                final WalletLog walletLog = WalletLogBuilder.create()
                    .action(WalletLogAction.SET_MONEY)
                    .adminName(sender.getName())
                    .buyerName(context.getName())
                    .amount(money)
                    .build();
                walletLogRepository.save(walletLog);
                sendWebHook(WalletLogAction.SET_MONEY, context.getName(), sender, money);

              });
        });

  }

  @Execute(name = "accountBalance", aliases = "balance")
  CompletableFuture<Void> accountBalance(
      final @Context CommandSender sender,
      final @Arg("networkPlayer") String name) {

    return supplyAsync(() -> walletUserFacade.findByName(name))
        .thenAccept(walletUser -> {
          if (walletUser == null) {
            BukkitMessage.from("&cNie znaleziono podanego gracza w bazie danych").deliver(sender);
            return;
          }
          BukkitMessage.from("&aStan konta gracza &2" + name + " &ato &2" + walletUser.getMoney())
              .deliver(sender);
        });
  }

  void sendWebHook(
      final WalletLogAction action,
      final String name,
      final CommandSender admin,
      final double money) {

    if (admin instanceof ConsoleCommandSender) {
      return;
    }

    final DiscordWebhook discordWebhook = new DiscordWebhook(WEB_HOOK_URL);
    final DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
    embed.setAuthor("Doładowania || Flamehub.pl", null, "https://i.imgur.com/B3lRUdp.png");
    embed.setColor(Color.YELLOW);
    embed.addField("**Akcja:**", action.toString(), true);
    embed.addField("**Administrator:**", admin.getName(), true);
    embed.addField("**Komu:**", name, true);
    embed.addField("**Ile:**", String.valueOf(money), true);
    embed.setImage("https://minotar.net/helm/" + name + "/100.png");
    embed.setTimestamp(Instant.now().toString());
    embed.setFooter("FlameHub.pl • " + TimeUtil.formatDate(Instant.now()),
        "https://i.imgur.com/B3lRUdp.png");
    discordWebhook.addEmbed(embed);
    discordWebhook.execute();

  }

  @Execute(name = "logs", aliases = {"history"})
  void logs(
      final @Context Player player,
      final @Arg WalletLogAction action,
      final @Arg("networkPlayer") String who) {

    final WalletLogGui walletLogGui = new WalletLogGui(walletLogRepository, flameDispatcher);
    walletLogGui.open(player, action, who);

  }

}

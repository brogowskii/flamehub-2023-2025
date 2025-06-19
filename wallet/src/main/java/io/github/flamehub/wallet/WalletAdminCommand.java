package io.github.flamehub.wallet;

import static io.github.flamehub.commons.util.CompletableFutures.NIL;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.flag.Flag;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.CommonsPlugin;
import io.github.flamehub.commons.bukkit.config.FlameConfigRefresherCommand;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.config.FlameConfigService;
import io.github.flamehub.commons.network.message.NetworkMessageFilter;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import org.bson.Document;
import org.bson.conversions.Bson;
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

  @Execute(name = "refund")
  void refund(@Context Player player, final @Flag("-a") boolean a) throws ParseException {

    if (!player.getName().equalsIgnoreCase("opalkamarcin")) {
      return;
    }

    final MongoClient mongoClient = CommonsPlugin.getInstance().getDatabaseConnector()
        .getMongoClient();
    MongoDatabase database = mongoClient.getDatabase("global");
    MongoCollection<Document> collection = database.getCollection("wallet_logs");

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    Date cutoff = sdf.parse("2025-04-12T00:00:00Z");

    Bson filter = Filters.and(
        Filters.gte("date", cutoff),
        Filters.eq("action", "BUY")
    );
    FindIterable<Document> results = collection.find(filter);

    int i = 0;
    double total = 0;
    for (Document doc : results) {
      String buyerName = doc.getString("buyerName");
      Number amount = doc.getDouble("amount");
      if (amount == null) {
        amount = doc.getInteger("amount");
      }
      total += amount.doubleValue();
      i++;
      if (!a) {
        player.sendMessage("Buyer: %s, Amount: %s%n".formatted(buyerName, amount));
      }
      else {
        add(player, buyerName, amount.doubleValue(), false);
      }
    }

    player.sendMessage("lacznie: " + i);
    player.sendMessage("vpln lacznie: " + total);
  }

  @Execute(name = "add")
  CompletableFuture<Void> add(
      final @Context CommandSender sender,
      final @Arg("networkPlayer") String name,
      final @Arg double money,
      final @Flag("-b") boolean broadcast) {

    if (money <= 0) {
      return BukkitMessage.from("&cWartośc pieniędzy musi być dodatnia").deliverAsync(sender);
    }

    return supplyAsync(() -> walletUserFacade.findByName(name))
        .thenCompose(context -> {
          if (context == null) {
            return BukkitMessage.from("&cNie znaleziono podanego gracza w bazie danych")
                .deliverAsync(sender);
          }

          walletUserFacade
              .update(context.getUniqueId(), mutator -> mutator.addMoney(BigDecimal.valueOf(money)));

          if (broadcast) {
            networkMessageService.send(
                messageService.getAsText("wallet.charge.vpln.broadcast")
                    .placeholder("{PLAYER}", context.getName())
                    .placeholder("{MONEY}", RoundUtil.round(money, 2))
                    .build(),
                NetworkMessageFilter.builder()
                    .idForHide("itemshop")
                    .build(),
                NetworkMessageType.CHAT
            );
          }

//          final WalletLog walletLog = WalletLogBuilder.create()
//              .action(WalletLogAction.ADD_MONEY)
//              .adminName(sender.getName())
//              .buyerName(context.getName())
//              .amount(money)
//              .build();
//          walletLogRepository.save(walletLog);
//          sendWebHook(WalletLogAction.ADD_MONEY, context.getName(), sender, money);

          return BukkitMessage.from("&aPomyślnie dodano &2%s &adla &2%s&a."
                  .formatted(money, context.getName()))
              .deliverAsync(sender);

        });
  }

  @Execute(name = "remove")
  CompletableFuture<Void> remove(
      final @Context CommandSender sender,
      final @Arg("networkPlayer") String name,
      final @Arg double money) {

    if (money <= 0) {
      return BukkitMessage.from("&cWartośc pieniędzy musi być dodatnia")
          .deliverAsync(sender);
    }

    return supplyAsync(() -> walletUserFacade.findByName(name))
        .thenCompose(context -> {

          if (context == null) {
            return BukkitMessage.from("&cNie znaleziono podanego gracza w bazie danych")
                .deliverAsync(sender);
          }

          walletUserFacade
              .update(context.getUniqueId(),
                  mutator -> mutator.subtractMoney(BigDecimal.valueOf(money)));

          BukkitMessage.from(
                  "&aUsunięto &2%s &az konta gracza %s".formatted(money, context.getName()))
              .deliver(sender);

//          final WalletLog walletLog = WalletLogBuilder.create()
//              .action(WalletLogAction.REMOVE_MONEY)
//              .adminName(sender.getName())
//              .buyerName(context.getName())
//              .amount(money)
//              .build();
//          walletLogRepository.save(walletLog);
//
//          sendWebHook(WalletLogAction.REMOVE_MONEY, context.getName(), sender, money);
          return NIL;

        });
  }

  @Execute(name = "set")
  CompletableFuture<Void> set(
      final @Context CommandSender sender,
      final @Arg("networkPlayer") String name,
      final @Arg double money) {

    if (money < 0) {
      return BukkitMessage.from("&cWartośc nie może byc ujemna").deliverAsync(sender);
    }

    return supplyAsync(() -> walletUserFacade.findByName(name))
        .thenCompose(context -> {

          if (context == null) {
            return BukkitMessage.from("&cNie znaleziono podanego gracza w bazie danych")
                .deliverAsync(sender);
          }

          walletUserFacade
              .update(context.getUniqueId(),
                  mutator -> mutator.setMoney(BigDecimal.valueOf(money)));
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

          return NIL;
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

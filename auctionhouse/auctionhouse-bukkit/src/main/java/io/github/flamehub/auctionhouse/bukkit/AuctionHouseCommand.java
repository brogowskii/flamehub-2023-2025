package io.github.flamehub.auctionhouse.bukkit;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.auctionhouse.bukkit.category.AuctionHouseCategoryConfig;
import io.github.flamehub.auctionhouse.bukkit.offer.AuctionHouseOfferCache;
import io.github.flamehub.auctionhouse.bukkit.offer.AuctionHouseOfferDto;
import io.github.flamehub.auctionhouse.commons.AuctionHouseConfig;
import io.github.flamehub.auctionhouse.commons.AuctionHouseJsonUtil;
import io.github.flamehub.auctionhouse.commons.AuctionHouseSeller;
import io.github.flamehub.auctionhouse.commons.action.AuctionHouseOfferAdd;
import io.github.flamehub.auctionhouse.commons.offer.AuctionHouseOffer;
import io.github.flamehub.auctionhouse.commons.offer.AuctionHouseOfferItem;
import io.github.flamehub.commons.bukkit.config.FlameConfigRefresher;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.util.NumberConverter;
import io.github.flamehub.commons.config.FlameConfigService;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.economy.EconomyFacade;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Command(name = "ah", aliases = {"rynek", "bazar", "auctionhouse"})
@Permission("server.commands.auctionhouse")
public final class AuctionHouseCommand extends FlameConfigRefresher {

  private final FlameDispatcher flameDispatcher;
  private final RedisMessenger redisMessenger;

  private final NetworkMessageService networkMessageService;
  private final AuctionHouseOfferCache auctionHouseOfferCache;
  private final AuctionHouseConfig auctionHouseConfig;
  private final AuctionHouseCategoryConfig auctionHouseCategoryConfig;
  private final EconomyFacade economyFacade;

  public AuctionHouseCommand(
      final FlameConfigService flameConfigService,
      final FlameDispatcher flameDispatcher,
      final RedisMessenger redisMessenger,
      final NetworkMessageService networkMessageService,
      final AuctionHouseOfferCache auctionHouseOfferCache,
      final AuctionHouseConfig auctionHouseConfig,
      final AuctionHouseCategoryConfig auctionHouseCategoryConfig,
      final EconomyFacade economyFacade
  ) {
    super(flameConfigService, AuctionHouseCategoryConfig.class);
    this.flameDispatcher = flameDispatcher;
    this.redisMessenger = redisMessenger;
    this.networkMessageService = networkMessageService;
    this.auctionHouseOfferCache = auctionHouseOfferCache;
    this.auctionHouseConfig = auctionHouseConfig;
    this.auctionHouseCategoryConfig = auctionHouseCategoryConfig;
    this.economyFacade = economyFacade;
  }

  @Execute
  void open(@Context Player player) {

    AuctionHouseGui auctionHouseGui = new AuctionHouseGui(
        this.flameDispatcher,
        this.auctionHouseConfig,
        this.redisMessenger,
        this.networkMessageService,
        this.auctionHouseOfferCache,
        this.auctionHouseCategoryConfig,
        this.economyFacade,
        player
    );
    auctionHouseGui.open();

  }

  @Execute(name = "wystaw", aliases = {"sell", "sprzedaj"})
  void sell(@Context Player player, @Arg("cena") double price) {

    ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
    if (itemInMainHand.getType().toString().contains("SHULKER_BOX")) {
      BukkitMessage.from("&cNie możesz wystawić tego przedmiotu.").send(player);
      return;
    }

    if (price <= 0) {
      BukkitMessage.from("&cCena oferty musi być większa od zera.").send(player);
      return;
    }

    if (price > 100000000000000000000.0) {
      BukkitMessage.from("&cNie przesadzaj z tą ceną.").send(player);
      return;
    }

    if (Double.isNaN(price) || Double.isInfinite(price)) {
      BukkitMessage.from("&cNieprawidłowa cena.").send(player);
      return;
    }

    if (itemInMainHand.getType().isAir()) {
      BukkitMessage.from("&cMusisz trzymać przedmiot w łapce.").send(player);
      return;
    }

    List<AuctionHouseOfferDto> auctionHouseOffers = this.auctionHouseOfferCache.values()
        .stream()
        .filter(auctionHouseOffer -> auctionHouseOffer.getSeller().getUniqueId()
            .equals(player.getUniqueId()))
        .toList();
    int size = auctionHouseOffers.size();

    int limit = AuctionHouseLimiter.getLimit(player);
    if (size + 1 > limit) {
      BukkitMessage.from(
              "&cNie możesz wystawić więcej przedmiotów na rynku! Twój limit wynosi: &4" + limit
                  + " &cwystawionych itemów na rynku.")
          .send(player);
      return;
    }

    ItemStack clone = itemInMainHand.clone();
    AuctionHouseOffer offer = new AuctionHouseOffer(
        UUID.randomUUID(),
        new AuctionHouseSeller(player.getUniqueId(), player.getName()),
        new AuctionHouseOfferItem(AuctionHouseSerializer.serialize(clone),
            clone.getType().toString()),
        BigDecimal.valueOf(price),
        Instant.now().plus(3, ChronoUnit.DAYS)
    );

    player.getInventory().setItemInMainHand(null);

    this.flameDispatcher.dispatchAsync(() -> {
      this.redisMessenger.publish(
          this.auctionHouseConfig.getMasterChannel(),
          new AuctionHouseOfferAdd(AuctionHouseJsonUtil.GSON.toJson(offer))
      );
    });

    BukkitMessage.from(
            "&aPomyślnie wystawiono przedmiot na rynek!",
            "&7Cena przedmiotu: &6$" + NumberConverter.convertNumber(price)
        )
        .send(player);

  }

//    @Async
//    @Execute(name = "selltest")
//    @Permission("server.commands.auctionhouse.selltest")
//    void selltest(@Context Player player, @Arg int offerCount) {
//
//        if (!player.getName().equals("opalkamarcin")) {
//            return;
//        }
//
//        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
//        if (itemInMainHand.getType().isAir()) {
//            return;
//        }
//
//        for (int i = 0; i < offerCount; i++) {
//
//            AuctionHouseOffer offer = new AuctionHouseOffer(
//                    UUID.randomUUID(),
//                    new AuctionHouseSeller(player.getUniqueId(), player.getName()),
//                    new AuctionHouseOfferItem(AuctionHouseSerializer.serialize(itemInMainHand), itemInMainHand.getType().toString()),
//                    BigDecimal.valueOf(100),
//                    Instant.now().plus(3, ChronoUnit.DAYS)
//            );
//
//                this.redisMessenger.publish(
//                        this.auctionHouseConfig.getMasterChannel(),
//                        new AuctionHouseOfferAdd(AuctionHouseJsonUtil.GSON.toJson(offer))
//                );
//        }
//
//    }

  @Execute(name = "reload")
  @Permission("server.commands.auctionhouse.reload")
  void reload(@Context CommandSender sender) {
    super.refreshConfigLocally(sender);
  }

  @Execute(name = "update")
  @Permission("server.commands.auctionhouse.update")
  void update(@Context CommandSender sender) {
    super.refreshConfigRemote(sender);
  }

}

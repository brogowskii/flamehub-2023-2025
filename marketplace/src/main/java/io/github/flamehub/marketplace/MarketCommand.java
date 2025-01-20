package io.github.flamehub.marketplace;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.marketplace.category.MarketCategoryConfig;
import io.github.flamehub.marketplace.offer.MarketOffer;
import io.github.flamehub.marketplace.offer.MarketOfferAdd;
import io.github.flamehub.marketplace.offer.MarketOfferCache;
import io.github.flamehub.marketplace.offer.MarketOfferItem;
import io.github.flamehub.marketplace.offer.MarketOfferRedisStorage;
import io.github.flamehub.marketplace.offer.MarketOfferRepository;
import io.github.flamehub.commons.bukkit.config.FlameConfigRefresher;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.util.NumberConverter;
import io.github.flamehub.commons.config.FlameConfigService;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.economy.EconomyFacade;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Command(name = "market", aliases = {"rynek", "bazar", "auctionhouse", "ah", "marketplace"})
@Permission("server.commands.marketplace")
public final class MarketCommand extends FlameConfigRefresher {

  private final FlameDispatcher flameDispatcher;
  private final RedisMessenger redisMessenger;

  private final NetworkServerCache networkServerCache;
  private final NetworkMessageService networkMessageService;
  private final MarketOfferCache marketOfferCache;
  private final MarketCategoryConfig marketCategoryConfig;
  private final MarketOfferRedisStorage marketOfferRedisStorage;
  private final MarketOfferRepository marketOfferRepository;
  private final EconomyFacade economyFacade;

  public MarketCommand(
      final FlameConfigService flameConfigService,
      final FlameDispatcher flameDispatcher,
      final RedisMessenger redisMessenger, final NetworkServerCache networkServerCache,
      final NetworkMessageService networkMessageService,
      final MarketOfferCache marketOfferCache,
      final MarketCategoryConfig marketCategoryConfig,
      final MarketOfferRedisStorage marketOfferRedisStorage,
      final MarketOfferRepository marketOfferRepository,
      final EconomyFacade economyFacade
  ) {
    super(flameConfigService, MarketCategoryConfig.class);
    this.flameDispatcher = flameDispatcher;
    this.redisMessenger = redisMessenger;
    this.networkServerCache = networkServerCache;
    this.networkMessageService = networkMessageService;
    this.marketOfferCache = marketOfferCache;
    this.marketCategoryConfig = marketCategoryConfig;
    this.marketOfferRedisStorage = marketOfferRedisStorage;
    this.marketOfferRepository = marketOfferRepository;
    this.economyFacade = economyFacade;
  }

  @Execute
  void open(final @Context Player player) {

    final MarketGui marketGui = new MarketGui(
        flameDispatcher,
        redisMessenger,
        networkServerCache,
        networkMessageService,
        marketOfferCache,
        marketCategoryConfig,
        marketOfferRedisStorage,
        marketOfferRepository,
        economyFacade,
        player
    );
    marketGui.open();

  }

  @Execute(name = "wystaw", aliases = {"sell", "sprzedaj"})
  void sell(final @Context Player player, final @Arg("cena") double price) {

    final ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
    if (itemInMainHand.getType().toString().contains("SHULKER_BOX")) {
      BukkitMessage.from("&cNie możesz wystawić tego przedmiotu.").deliver(player);
      return;
    }

    if (price <= 0) {
      BukkitMessage.from("&cCena oferty musi być większa od zera.").deliver(player);
      return;
    }

    if (price > 100000000000000000000.0) {
      BukkitMessage.from("&cNie przesadzaj z tą ceną.").deliver(player);
      return;
    }

    if (Double.isNaN(price) || Double.isInfinite(price)) {
      BukkitMessage.from("&cNieprawidłowa cena.").deliver(player);
      return;
    }

    if (itemInMainHand.getType().isAir()) {
      BukkitMessage.from("&cMusisz trzymać przedmiot w łapce.").deliver(player);
      return;
    }

    final List<MarketOffer> marketOffers = marketOfferCache.values()
        .stream()
        .filter(auctionHouseOffer -> auctionHouseOffer.getSeller().getUniqueId()
            .equals(player.getUniqueId()))
        .toList();

    final int size = marketOffers.size();
    final int limit = MarketLimiter.getLimit(player);
    if (size + 1 > limit) {
      BukkitMessage.from(
              "&cNie możesz wystawić więcej przedmiotów na rynku! Twój limit wynosi: &4" + limit
                  + " &cwystawionych itemów na rynku.")
          .deliver(player);
      return;
    }

    final ItemStack clone = itemInMainHand.clone();
    final MarketOffer offer = new MarketOffer(
        UUID.randomUUID(),
        new MarketSeller(player.getUniqueId(), player.getName()),
        new MarketOfferItem(MarketSerializer.serialize(clone)),
        BigDecimal.valueOf(price),
        Instant.now().plus(3, ChronoUnit.DAYS)
    );

    player.getInventory().setItemInMainHand(null);
    flameDispatcher.dispatchAsync(() -> {
      redisMessenger.publish(
          networkServerCache.getCurrent().getCategory() + "_auctionhouse_actions",
          new MarketOfferAdd(offer)
      );

      marketOfferRepository.save(offer);
      marketOfferRedisStorage.add(offer.getOfferId());

    });

    BukkitMessage.from(
            "&aPomyślnie wystawiono przedmiot na rynek!",
            "&7Cena przedmiotu: &6$" + NumberConverter.convertNumber(price)
        )
        .deliver(player);

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
  void reload(final @Context CommandSender sender) {
    super.refreshConfigLocally(sender);
  }

  @Execute(name = "update")
  @Permission("server.commands.auctionhouse.update")
  void update(final @Context CommandSender sender) {
    super.refreshConfigRemote(sender);
  }

}

package io.github.flamehub.marketplace;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.cooldown.CooldownState;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import io.github.flamehub.marketplace.category.MarketCategoryConfig;
import io.github.flamehub.marketplace.offer.MarketOffer;
import io.github.flamehub.marketplace.offer.MarketOfferCache;
import io.github.flamehub.marketplace.offer.MarketOfferRedisStorage;
import io.github.flamehub.marketplace.offer.MarketOfferRepository;
import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.command.argument.LocationArgument;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.handler.CooldownStateResultHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.economy.EconomyFacade;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class MarketPlugin extends BukkitModule {

  private EconomyFacade economyFacade;

  private NetworkMessageService networkMessageService;
  private MarketCategoryConfig marketCategoryConfig;
  private MarketOfferCache marketOfferCache;
  private MarketOfferRepository marketOfferRepository;
  private MarketOfferRedisStorage marketOfferRedisStorage;

  @Override
  public void onEnable() {
    super.onEnable();
    economyFacade = getService(EconomyFacade.class);

    networkMessageService = new NetworkMessageService(redisMessenger, "network_messages");

    marketCategoryConfig = flameConfigService.getOrCreate(getDataFolder(),
        MarketCategoryConfig.class);

    marketOfferCache = new MarketOfferCache();
    marketOfferRepository = new MarketOfferRepository(
        DatastoreFactory.create(
            databaseConnector.getMongoClient(),
            networkServerCache.getCurrent().getCategory(),
            MarketOffer.class
        )
    );
    marketOfferRedisStorage = new MarketOfferRedisStorage(
        redisService,
        networkServerCache
    );

    marketOfferRepository.loadAll()
        .forEach(offer -> {
          marketOfferCache.add(offer.getOfferId(), offer);
          if (!marketOfferRedisStorage.exists(offer.getOfferId())) {
            marketOfferRedisStorage.add(offer.getOfferId());
            System.out.println("Offer " + offer.getOfferId() + " added to Redis");
          }
        });

    redisMessenger.subscribe(
        networkServerCache.getCurrent().getCategory() + "_auctionhouse_actions",
        new MarketHandler(marketOfferCache));

    LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("flamehub-auctionhouse")
            .nativePermissions(false)
        )
        .argument(Location.class, new LocationArgument())
        .argument(Player.class, new PlayerArgument(messagesService))
        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))
        .result(CooldownState.class, new CooldownStateResultHandlerImpl(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(messagesService))

        .commands(LiteCommandsAnnotations.of(
            new MarketCommand(flameConfigService, flameDispatcher,
                redisMessenger, networkServerCache,
                networkMessageService, marketOfferCache,
                marketCategoryConfig, marketOfferRedisStorage,
                marketOfferRepository, economyFacade)
        ))

        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();

  }
}
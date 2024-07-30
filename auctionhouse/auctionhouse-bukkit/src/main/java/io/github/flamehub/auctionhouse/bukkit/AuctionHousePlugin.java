package io.github.flamehub.auctionhouse.bukkit;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.cooldown.CooldownState;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import io.github.flamehub.auctionhouse.bukkit.category.AuctionHouseCategoryConfig;
import io.github.flamehub.auctionhouse.bukkit.offer.AuctionHouseOfferCache;
import io.github.flamehub.auctionhouse.bukkit.offer.AuctionHouseOfferDto;
import io.github.flamehub.auctionhouse.commons.AuctionHouseConfig;
import io.github.flamehub.auctionhouse.commons.offer.AuctionHouseOffer;
import io.github.flamehub.auctionhouse.commons.offer.AuctionHouseOfferRepository;
import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.command.argument.LocationArgument;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.handler.CooldownStateResultHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.economy.EconomyFacade;
import java.io.IOException;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class AuctionHousePlugin extends BukkitModule {

  private EconomyFacade economyFacade;

  private NetworkMessageService networkMessageService;
  private AuctionHouseConfig auctionHouseConfig;
  private AuctionHouseCategoryConfig auctionHouseCategoryConfig;
  private AuctionHouseOfferCache auctionHouseOfferCache;
  private AuctionHouseOfferRepository auctionHouseOfferRepository;

  @Override
  public void onEnable() {
    super.onEnable();
    this.economyFacade = getService(EconomyFacade.class);

    this.networkMessageService = new NetworkMessageService(this.redisMessenger, "network_messages");

    this.auctionHouseCategoryConfig = this.flameConfigService.getOrCreate(this.getDataFolder(),
        AuctionHouseCategoryConfig.class);
    this.auctionHouseConfig = this.flameConfigService.getOrCreate(this.getDataFolder(),
        AuctionHouseConfig.class);

    this.auctionHouseOfferCache = new AuctionHouseOfferCache();
    this.auctionHouseOfferRepository = new AuctionHouseOfferRepository(
        DatastoreFactory.create(
            this.databaseConnector.getMongoClient(),
            this.auctionHouseConfig.getDatabase(),
            AuctionHouseOffer.class
        ),
        AuctionHouseOffer.class
    );

    this.auctionHouseOfferRepository.loadAll()
        .forEach(offer -> {

          AuctionHouseOfferDto auctionHouseOfferDto = new AuctionHouseOfferDto(offer.getOfferId(),
              offer.getSeller(), offer.getItem(), offer.getPrice(), offer.getExpirationTime());
          ItemStack itemStack;
          try {
            itemStack = AuctionHouseSerializer.deserialize(
                auctionHouseOfferDto.getItem().getSerializedItemStack());
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
          auctionHouseOfferDto.setItemStack(itemStack);

          this.auctionHouseOfferCache.add(offer.getOfferId(), auctionHouseOfferDto);
        });

    this.redisMessenger.subscribe(this.auctionHouseConfig.getSlavesUpdateChannel(),
        new AuctionHouseHandler(this.auctionHouseOfferCache));

    LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("flamehub-auctionhouse")
            .nativePermissions(false)
        )
        .argument(Location.class, new LocationArgument())
        .argument(Player.class, new PlayerArgument(this.messagesService))
        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))
        .result(CooldownState.class, new CooldownStateResultHandlerImpl(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(this.messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(this.messagesService))

        .commands(LiteCommandsAnnotations.of(
            new AuctionHouseCommand(flameConfigService, flameDispatcher, redisMessenger,
                networkMessageService, auctionHouseOfferCache, auctionHouseConfig,
                auctionHouseCategoryConfig, economyFacade)
        ))

        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();

  }
}
package io.github.flamehub.coinflip;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import io.github.flamehub.coinflip.user.CoinFlipUser;
import io.github.flamehub.coinflip.user.CoinFlipUserCache;
import io.github.flamehub.coinflip.user.CoinFlipUserFactory;
import io.github.flamehub.coinflip.user.CoinFlipUserListener;
import io.github.flamehub.coinflip.user.CoinFlipUserRepository;
import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.server.NetworkServerContext;
import org.bukkit.entity.Player;

public final class CoinFlipPlugin extends BukkitModule {

  private NetworkMessageService networkMessageService;

  private CoinFlipConfig coinFlipConfig;
  private CoinFlipGameCache coinFlipGameCache;

  private CoinFlipUserCache coinFlipUserCache;
  private CoinFlipUserFactory coinFlipUserFactory;
  private CoinFlipUserRepository coinFlipUserRepository;

  @Override
  public void onEnable() {
    super.onEnable();

    coinFlipConfig = flameConfigService.getOrCreate( CoinFlipConfig.class);
    coinFlipGameCache = new CoinFlipGameCache(
        redisMessenger,
        redisService,
        networkServerFacade.getCurrent().getCategory() + "_coinflip"
    );
    networkMessageService = new NetworkMessageService(redisMessenger, "network_messages");

    coinFlipUserRepository = new CoinFlipUserRepository(
        DatastoreFactory.create(
            databaseConnector.getMongoClient(),
            networkServerFacade.getCurrent().getCategory(),
            CoinFlipUser.class
        )
    );
    coinFlipUserCache = new CoinFlipUserCache(redisMessenger, redisService, coinFlipUserRepository);
    coinFlipUserFactory = new CoinFlipUserFactory();

    getServer().getPluginManager().registerEvents(new CoinFlipUserListener(
        flameDispatcher, getServer().getPluginManager(), coinFlipUserCache,
        coinFlipUserRepository, coinFlipUserFactory), this);

    redisMessenger.subscribe(
        NetworkServerContext.CURRENT_CATEGORY + ":coinflip",
        new CoinFlipHandler(flameDispatcher)
    );

    LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("coinflip")
            .nativePermissions(false)
        )
        .argument(Player.class, new PlayerArgument(messagesService))
        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(messagesService))

        .commands(LiteCommandsAnnotations.of(
            new CoinFlipCommand(
                redisMessenger,
                networkMessageService,
                flameConfigService,
                coinFlipConfig,
                coinFlipGameCache,
                coinFlipUserCache
            )
        ))

        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();
  }

  @Override
  public void onDisable() {
    super.onDisable();
  }
}

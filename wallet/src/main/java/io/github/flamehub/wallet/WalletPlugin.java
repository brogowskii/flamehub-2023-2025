package io.github.flamehub.wallet;

import dev.morphia.Datastore;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.argument.ArgumentKey;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.LiteBukkitSettings;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import dev.rollczi.litecommands.strict.StrictMode;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.command.argument.LocationArgument;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.argument.WorldArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.wallet.item.WalletOfferConfig;
import io.github.flamehub.wallet.log.WalletLog;
import io.github.flamehub.wallet.log.WalletLogRepository;
import io.github.flamehub.wallet.user.WalletUser;
import io.github.flamehub.wallet.user.WalletUserFacade;
import io.github.flamehub.wallet.user.WalletUserFacadeCreator;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class WalletPlugin extends BukkitModule {

  private NetworkMessageService networkMessageService;

  private WalletUserFacade walletUserFacade;
  private WalletLogRepository walletLogRepository;
  private WalletOfferConfig walletOfferConfig;

  @Override
  public void onEnable() {
    super.onEnable();

    this.walletOfferConfig = flameConfigService.getOrCreate(getDataFolder(),
        WalletOfferConfig.class);
    this.networkMessageService = new NetworkMessageService(redisMessenger, "network_messages");

    Datastore global = DatastoreFactory.create(databaseConnector.getMongoClient(), "global",
        WalletUser.class, WalletLog.class);

    final LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> liteCommandsBuilder = LiteBukkitFactory.builder();
    this.walletLogRepository = new WalletLogRepository(global, WalletLog.class);
    this.walletUserFacade = WalletUserFacadeCreator.createWalletUserFacade(flameDispatcher,
        liteCommandsBuilder, messagesService, getServer().getPluginManager(), this, global,
        redisMessenger, redisService);

    liteCommandsBuilder
        .settings(settings -> settings
            .fallbackPrefix("flamehub-wallet")
            .nativePermissions(false)
        )
        .argument(Location.class, new LocationArgument())
        .argument(World.class, new WorldArgument())
        .argument(Player.class, new PlayerArgument(messagesService))
        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(messagesService))

        .commands(LiteCommandsAnnotations.of(
            new WalletCommand(flameDispatcher, networkMessageService,
                messagesService, walletUserFacade, walletOfferConfig, walletLogRepository),
            new WalletAdminCommand(networkMessageService, messagesService,
                flameConfigService, flameDispatcher, walletUserFacade, walletLogRepository)
        ))
        .argumentSuggester(String.class, ArgumentKey.of("networkPlayer"),
            (invocation, argument, context) -> networkPlayerCache.values()
                .stream()
                .map(NetworkPlayer::getName)
                .collect(SuggestionResult.collector())
        )
        .schematicGenerator(SchematicFormat.angleBrackets())
        .strictMode(StrictMode.ENABLED)
        .build();

    setupPlaceholders();

  }

  void setupPlaceholders() {
    new WalletPlaceholder(walletUserFacade).register();
  }

}

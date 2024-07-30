package io.github.flamehub.wallet;

import dev.morphia.Datastore;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.argument.ArgumentKey;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.command.argument.LocationArgument;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.argument.WorldArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.bukkit.user.UserDatabaseListener;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.wallet.api.WalletUser;
import io.github.flamehub.wallet.api.WalletUserRepository;
import io.github.flamehub.wallet.item.WalletOfferConfig;
import io.github.flamehub.wallet.log.WalletLog;
import io.github.flamehub.wallet.log.WalletLogRepository;
import io.github.flamehub.wallet.user.WalletUserArgument;
import io.github.flamehub.wallet.user.WalletUserCache;
import io.github.flamehub.wallet.user.WalletUserContextual;
import io.github.flamehub.wallet.user.WalletUserFactory;
import io.github.flamehub.wallet.user.WalletUserUpdateHandler;
import io.github.flamehub.wallet.user.WalletUserUpdater;
import io.github.flamehub.wallet.user.api.WalletUserApiHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public final class WalletPlugin extends BukkitModule {

  private NetworkMessageService networkMessageService;

  private WalletUserCache walletUserCache;
  private WalletUserFactory walletUserFactory;
  private WalletUserRepository walletUserRepository;
  private WalletUserUpdater walletUserUpdater;
  private WalletLogRepository walletLogRepository;

  private WalletOfferConfig walletOfferConfig;

  @Override
  public void onEnable() {
    super.onEnable();

    this.walletOfferConfig = this.flameConfigService.getOrCreate(this.getDataFolder(),
        WalletOfferConfig.class);
    this.networkMessageService = new NetworkMessageService(this.redisMessenger, "network_messages");

    Datastore global = DatastoreFactory.create(this.databaseConnector.getMongoClient(), "global",
        WalletUser.class, WalletLog.class);
    this.walletUserRepository = new WalletUserRepository(global, WalletUser.class);
    this.walletUserFactory = new WalletUserFactory();
    this.walletUserCache = new WalletUserCache(this.walletUserRepository);
    this.walletUserUpdater = new WalletUserUpdater(flameDispatcher, this.networkPlayerCache,
        networkServerCache, this.walletUserRepository, this.redisMessenger);

    this.walletLogRepository = new WalletLogRepository(global, WalletLog.class);

    this.redisMessenger.subscribe(this.networkServerCache.getCurrent().getName(),
        new WalletUserUpdateHandler(this.walletUserCache, this.walletUserRepository));
    this.redisMessenger.subscribe(this.networkServerCache.getCurrent().getName(),
        new WalletUserApiHandler(this.walletUserCache, this.walletUserRepository));

    setupListeners();
    setupCommands();
    setupPlaceholders();

  }

  void setupPlaceholders() {
    new WalletPlaceholder(this.walletUserCache).register();
  }

  void setupListeners() {
    PluginManager pluginManager = this.getServer().getPluginManager();
    pluginManager.registerEvents(
        new UserDatabaseListener<>(
            this.flameDispatcher,
            pluginManager,
            this.walletUserCache,
            this.walletUserRepository,
            this.walletUserFactory
        ),
        this
    );
  }

  void setupCommands() {
    LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("flamehub-wallet")
            .nativePermissions(false)
        )
        .argument(Location.class, new LocationArgument())
        .argument(World.class, new WorldArgument())
        .argument(Player.class, new PlayerArgument(this.messagesService))
        .argument(WalletUser.class,
            new WalletUserArgument(this.walletUserCache, this.messagesService))

        .context(WalletUser.class, new WalletUserContextual(this.walletUserCache))
        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(this.messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(this.messagesService))

        .commands(LiteCommandsAnnotations.of(
            new WalletCommand(this.flameDispatcher, this.networkMessageService,
                this.messagesService, this.walletUserCache, this.walletOfferConfig,
                this.walletUserRepository, walletLogRepository),
            new WalletAdminCommand(this.networkMessageService, this.messagesService,
                this.flameConfigService, this.walletUserUpdater, walletLogRepository)
        ))
        .argumentSuggester(String.class, ArgumentKey.of("playerName"),
            (invocation, argument, context) -> Bukkit.getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .collect(SuggestionResult.collector())
        )
        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();
  }

}

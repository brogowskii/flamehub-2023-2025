package io.github.flamehub.wallet;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.argument.ArgumentKey;
import dev.rollczi.litecommands.bukkit.LiteCommandsBukkit;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.json.gson.JsonGsonConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import io.github.flamehub.commons.bukkit.BukkitPlugin;
import io.github.flamehub.commons.bukkit.command.argument.LocationArgument;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.argument.WorldArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.user.UserDatabaseListener;
import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.wallet.item.WalletOfferConfig;
import io.github.flamehub.wallet.api.WalletUser;
import io.github.flamehub.wallet.user.WalletUserCache;
import io.github.flamehub.wallet.user.WalletUserFactory;
import io.github.flamehub.wallet.api.WalletUserRepository;
import io.github.flamehub.wallet.user.api.WalletUserApiHandler;
import io.github.flamehub.wallet.user.update.WalletUserUpdateHandler;
import io.github.flamehub.wallet.user.update.WalletUserUpdater;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public final class WalletPlugin extends BukkitPlugin {

    private DatabaseConnector databaseConnector;
    private RedisMessenger redisMessenger;

    private NetworkPlayerCache networkPlayerCache;
    private NetworkServerCache networkServerCache;
    private NetworkMessageService networkMessageService;
    private BukkitMessagesService messagesService;

    private WalletUserCache walletUserCache;
    private WalletUserFactory walletUserFactory;
    private WalletUserRepository walletUserRepository;
    private WalletUserUpdater walletUserUpdater;

    private WalletOfferConfig walletOfferConfig;

    @Override
    public void onEnable() {

        this.walletOfferConfig = ConfigManager.create(WalletOfferConfig.class, (it) -> {
            it.withConfigurer(new JsonGsonConfigurer(), new SerdesBukkit());
            it.withBindFile(this.getDataFolder() + "/offers.json");
            it.saveDefaults();
            it.load(true);
        });

        this.databaseConnector = getService(DatabaseConnector.class);
        this.redisMessenger = getService(RedisMessenger.class);
        this.messagesService = getService(BukkitMessagesService.class);
        this.networkPlayerCache = getService(NetworkPlayerCache.class);
        this.networkServerCache = getService(NetworkServerCache.class);

        this.networkMessageService = new NetworkMessageService(this.redisMessenger, "network_messages");

        this.walletUserRepository = new WalletUserRepository(DatastoreFactory.create(this.databaseConnector.getMongoClient(), "global", WalletUser.class), WalletUser.class);
        this.walletUserFactory = new WalletUserFactory();
        this.walletUserCache = new WalletUserCache(this.walletUserRepository);
        this.walletUserUpdater = new WalletUserUpdater(this.networkPlayerCache, this.walletUserRepository, this.redisMessenger);

        this.redisMessenger.subscribe(this.networkServerCache.getCurrent().getName(), new WalletUserUpdateHandler(this.walletUserCache, this.walletUserRepository));
        this.redisMessenger.subscribe("wallet_api_update", new WalletUserApiHandler(this.walletUserCache));

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
        LiteCommandsBukkit.builder()
                .settings(settings -> settings
                        .fallbackPrefix("flamehub-wallet")
                        .nativePermissions(false)
                )
                .argument(Location.class, new LocationArgument())
                .argument(World.class, new WorldArgument())
                .argument(Player.class, new PlayerArgument(this.messagesService))

                .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry()))

                .missingPermission(new MissingPermissionHandlerImpl(this.messagesService))
                .invalidUsage(new InvalidUsageHandlerImpl(this.messagesService))

                .commands(LiteCommandsAnnotations.of(
                        new WalletCommand(this.flameDispatcher, this.networkMessageService, this.messagesService, this.walletUserCache, this.walletOfferConfig, this.walletUserRepository),
                        new WalletAdminCommand(this.networkMessageService, this.messagesService, this.walletOfferConfig, this.walletUserCache, this.walletUserUpdater)
                ))
                .argumentSuggester(String.class, ArgumentKey.of("playerName"), (invocation, argument, context) -> Bukkit.getOnlinePlayers()
                        .stream()
                        .map(Player::getName)
                        .collect(SuggestionResult.collector())
                )
                .schematicGenerator(SchematicFormat.angleBrackets())
                .build();
    }

}

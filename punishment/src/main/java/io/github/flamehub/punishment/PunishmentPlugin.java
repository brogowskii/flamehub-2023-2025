package io.github.flamehub.punishment;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.argument.ArgumentKey;
import dev.rollczi.litecommands.bukkit.LiteCommandsBukkit;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import io.github.flamehub.commons.bukkit.BukkitPlugin;
import io.github.flamehub.commons.bukkit.command.argument.LocationArgument;
import io.github.flamehub.commons.bukkit.command.argument.WorldArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.network.player.NetworkPlayerArgument;
import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.server.NetworkServerCache;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;

public final class PunishmentPlugin extends BukkitPlugin {

    private RedisMessenger redisMessenger;
    private DatabaseConnector databaseConnector;
    private BukkitMessagesService messagesService;

    private NetworkServerCache networkServerCache;
    private NetworkPlayerCache networkPlayerCache;
    private NetworkMessageService networkMessageService;

    private PunishmentRepository punishmentRepository;

    @Override
    public void onEnable() {

        this.redisMessenger = getService(RedisMessenger.class);
        this.databaseConnector = getService(DatabaseConnector.class);
        this.networkServerCache = getService(NetworkServerCache.class);
        this.networkPlayerCache = getService(NetworkPlayerCache.class);
        this.messagesService = getService(BukkitMessagesService.class);

        this.networkMessageService = new NetworkMessageService(this.redisMessenger, "network_messages");
        this.punishmentRepository = new PunishmentRepository(DatastoreFactory.create(this.databaseConnector.getMongoClient(), "global", Punishment.class), Punishment.class);

        this.getServer().getServicesManager().register(PunishmentRepository.class, this.punishmentRepository, this, ServicePriority.Normal);

        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new PunishmentListener(this.messagesService, this.punishmentRepository, this.networkServerCache), this);

        LiteCommandsBukkit.builder()
                .settings(settings -> settings
                        .fallbackPrefix("flamehub-punishment")
                        .nativePermissions(false)
                )
                .argument(Location.class, new LocationArgument())
                .argument(World.class, new WorldArgument())
                .argument(NetworkPlayer.class, new NetworkPlayerArgument(this.messagesService, this.networkPlayerCache, this.networkServerCache))

                .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

                .missingPermission(new MissingPermissionHandlerImpl(this.messagesService))
                .invalidUsage(new InvalidUsageHandlerImpl(this.messagesService))

                .commands(LiteCommandsAnnotations.of(
                        new PunishmentCommand(this.flameDispatcher, this.punishmentRepository, this.messagesService, this.networkMessageService, this.networkServerCache)
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

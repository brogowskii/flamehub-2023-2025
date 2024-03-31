package io.github.flamehub.code;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteCommandsBukkit;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import io.github.flamehub.commons.bukkit.BukkitPlugin;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.user.UserDatabaseListener;
import io.github.flamehub.commons.config.MongoConfigService;
import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.timeplayed.user.TimePlayedUserCache;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public final class CodePlugin extends BukkitPlugin {

    private MongoConfigService mongoConfigService;
    private DatabaseConnector databaseConnector;
    private BukkitMessagesService messagesService;
    private TimePlayedUserCache timePlayedUserCache;

    private CodeConfig codeConfig;

    private CodeUserCache codeUserCache;
    private CodeUserRepository codeUserRepository;
    private CodeUserFactory codeUserFactory;

    @Override
    public void onEnable() {

        this.databaseConnector = this.getService(DatabaseConnector.class);
        this.timePlayedUserCache = this.getService(TimePlayedUserCache.class);
        this.messagesService = this.getService(BukkitMessagesService.class);
        this.mongoConfigService = this.getService(MongoConfigService.class);

        this.codeConfig = this.mongoConfigService.findOrCreate(CodeConfig.class, "codes", CodeConfig::new);
        this.codeUserRepository = new CodeUserRepository(DatastoreFactory.create(this.databaseConnector.getMongoClient(), this.codeConfig.getDatabase(), CodeUser.class), CodeUser.class);
        this.codeUserCache = new CodeUserCache(this.codeUserRepository);
        this.codeUserFactory = new CodeUserFactory();

        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new UserDatabaseListener<>(this.flameDispatcher, pluginManager, this.codeUserCache, this.codeUserRepository, this.codeUserFactory), this);

        LiteCommandsBukkit.builder()
                .settings(settings -> settings
                        .fallbackPrefix("codes")
                        .nativePermissions(false)
                )
                .argument(Player.class, new PlayerArgument(this.messagesService))
                .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

                .missingPermission(new MissingPermissionHandlerImpl(this.messagesService))
                .invalidUsage(new InvalidUsageHandlerImpl(this.messagesService))

                .commands(LiteCommandsAnnotations.of(
                        new CodeCommand(this.timePlayedUserCache, this.flameDispatcher, mongoConfigService, this.codeConfig, this.codeUserCache, this.codeUserRepository)
                ))

                .schematicGenerator(SchematicFormat.angleBrackets())
                .build();

    }
}

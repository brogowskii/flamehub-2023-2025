package io.github.flamehub.checksystem;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteCommandsBukkit;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.json.gson.JsonGsonConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import io.github.flamehub.checksystem.history.CheckHistory;
import io.github.flamehub.checksystem.history.CheckHistoryCommand;
import io.github.flamehub.checksystem.history.CheckHistoryRepository;
import io.github.flamehub.commons.bukkit.BukkitPlugin;
import io.github.flamehub.commons.bukkit.CommonsPlugin;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.database.DatastoreFactory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public final class CheckSystemPlugin extends BukkitPlugin {

    private DatabaseConnector databaseConnector;
    private BukkitMessagesService messagesService;
    private CheckService checkService;
    private CheckConfig checkConfig;
    private CheckHistoryRepository checkHistoryRepository;

    @Override
    public void onEnable() {

        this.databaseConnector = this.getService(DatabaseConnector.class);
        this.messagesService = this.getService(BukkitMessagesService.class);

        this.checkConfig = ConfigManager.create(CheckConfig.class, (it) -> {
            it.withConfigurer(new JsonGsonConfigurer(), new SerdesBukkit());
            it.withBindFile(this.getDataFolder() + "/checkConfig.json");
            it.saveDefaults();
            it.load(true);
        });
        this.checkService = new CheckService();
        this.checkHistoryRepository = new CheckHistoryRepository(
                DatastoreFactory.create(
                        this.databaseConnector.getMongoClient(),
                        CommonsPlugin.getInstance().getNetworkServerCache().getCurrent().getCategory(),
                        CheckHistory.class)
        );

        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new CheckListener(this.flameDispatcher, this.checkService, this.checkConfig, this.checkHistoryRepository), this);

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
                        new CheckAdmissionCommand(flameDispatcher, this.checkService, checkConfig, checkHistoryRepository),
                        new CheckCommand(flameDispatcher, this.checkService, this.checkConfig, checkHistoryRepository),
                        new CheckHistoryCommand(this.flameDispatcher, this.checkHistoryRepository)
                ))

                .schematicGenerator(SchematicFormat.angleBrackets())
                .build();

    }
}

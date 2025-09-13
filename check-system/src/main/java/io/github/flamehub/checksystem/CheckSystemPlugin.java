package io.github.flamehub.checksystem;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import io.github.flamehub.checksystem.history.CheckHistory;
import io.github.flamehub.checksystem.history.CheckHistoryCommand;
import io.github.flamehub.checksystem.history.CheckHistoryRepository;
import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.CommonsPlugin;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.database.DatastoreFactory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public final class CheckSystemPlugin extends BukkitModule {

  private CheckService checkService;
  private CheckConfig checkConfig;
  private CheckHistoryRepository checkHistoryRepository;

  @Override
  public void onEnable() {
    super.onEnable();

    checkConfig = flameConfigService.getOrCreate(CheckConfig.class);

    checkService = new CheckService();
    checkHistoryRepository = new CheckHistoryRepository(
        DatastoreFactory.create(
            databaseConnector.getMongoClient(),
            CommonsPlugin.getInstance().getNetworkServerFacade().getCurrent().getCategory(),
            CheckHistory.class)
    );

    final PluginManager pluginManager = getServer().getPluginManager();
    pluginManager.registerEvents(
        new CheckListener(flameDispatcher, checkService, checkConfig,
            checkHistoryRepository), this);

    LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("check-system")
            .nativePermissions(false)
        )
        .argument(Player.class, new PlayerArgument(messagesService))
        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(messagesService))

        .commands(LiteCommandsAnnotations.of(
            new CheckAdmissionCommand(flameDispatcher, checkService, checkConfig,
                checkHistoryRepository),
            new CheckCommand(flameConfigService, flameDispatcher, checkService,
                checkConfig, checkHistoryRepository),
            new CheckHistoryCommand(flameDispatcher, checkHistoryRepository)
        ))

        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();

  }
}

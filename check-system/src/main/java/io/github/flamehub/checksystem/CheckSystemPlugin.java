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

    this.checkConfig = this.flameConfigService.getOrCreate(this.getDataFolder(), CheckConfig.class);

    this.checkService = new CheckService();
    this.checkHistoryRepository = new CheckHistoryRepository(
        DatastoreFactory.create(
            this.databaseConnector.getMongoClient(),
            CommonsPlugin.getInstance().getNetworkServerCache().getCurrent().getCategory(),
            CheckHistory.class)
    );

    PluginManager pluginManager = this.getServer().getPluginManager();
    pluginManager.registerEvents(
        new CheckListener(this.flameDispatcher, this.checkService, this.checkConfig,
            this.checkHistoryRepository), this);

    LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("codes")
            .nativePermissions(false)
        )
        .argument(Player.class, new PlayerArgument(this.messagesService))
        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(this.messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(this.messagesService))

        .commands(LiteCommandsAnnotations.of(
            new CheckAdmissionCommand(this.flameDispatcher, this.checkService, this.checkConfig,
                this.checkHistoryRepository),
            new CheckCommand(this.flameConfigService, this.flameDispatcher, this.checkService,
                this.checkConfig, this.checkHistoryRepository),
            new CheckHistoryCommand(this.flameDispatcher, this.checkHistoryRepository)
        ))

        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();

  }
}

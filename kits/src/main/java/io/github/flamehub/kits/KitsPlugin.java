package io.github.flamehub.kits;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.command.argument.LocationArgument;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.argument.WorldArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.user.UserDatabaseListener;
import io.github.flamehub.commons.config.FlameConfigService;
import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.kits.kit.Kit;
import io.github.flamehub.kits.kit.KitArgument;
import io.github.flamehub.kits.kit.KitCommand;
import io.github.flamehub.kits.kit.KitListener;
import io.github.flamehub.kits.kit.management.KitManagementCommand;
import io.github.flamehub.kits.kit.management.KitManagementListener;
import io.github.flamehub.kits.user.KitUser;
import io.github.flamehub.kits.user.KitUserCache;
import io.github.flamehub.kits.user.KitUserFactory;
import io.github.flamehub.kits.user.KitUserRepository;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public final class KitsPlugin extends BukkitModule {

  private KitsConfig kitsConfig;

  private KitUserCache kitUserCache;
  private KitUserFactory kitUserFactory;
  private KitUserRepository kitUserRepository;

  @Override
  public void onEnable() {
    super.onEnable();

    databaseConnector = getService(DatabaseConnector.class);
    messagesService = getService(BukkitMessagesService.class);
    flameConfigService = getService(FlameConfigService.class);

    kitsConfig = flameConfigService.getOrCreate(KitsConfig.class);
    kitUserRepository = new KitUserRepository(
        DatastoreFactory.create(
            databaseConnector.getMongoClient(),
            networkServerFacade.getCurrent().getCategory(),
            KitUser.class
        ),
        KitUser.class
    );
    kitUserCache = new KitUserCache(kitUserRepository);
    kitUserFactory = new KitUserFactory();

    setupCommands();
    setupListeners();

  }

  void setupListeners() {
    final PluginManager pluginManager = getServer().getPluginManager();
    pluginManager.registerEvents(
        new UserDatabaseListener<>(getFlameDispatcher(), pluginManager, kitUserCache,
            kitUserRepository, kitUserFactory), this);
    pluginManager.registerEvents(
        new KitManagementListener(flameConfigService, kitsConfig), this);
    pluginManager.registerEvents(new KitListener(kitsConfig), this);
  }

  void setupCommands() {
    LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("flamehub-kits")
            .nativePermissions(false)
        )
        .argument(Location.class, new LocationArgument())
        .argument(World.class, new WorldArgument())
        .argument(Player.class, new PlayerArgument(messagesService))
        .argument(Kit.class, new KitArgument(kitsConfig))

        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(messagesService))

        .commands(LiteCommandsAnnotations.of(
            new KitCommand(getFlameDispatcher(), kitsConfig, kitUserCache,
                kitUserRepository),
            new KitManagementCommand(flameConfigService, kitsConfig)
        ))

        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();
  }

}
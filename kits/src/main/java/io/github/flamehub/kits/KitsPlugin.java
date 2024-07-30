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

    this.databaseConnector = getService(DatabaseConnector.class);
    this.messagesService = getService(BukkitMessagesService.class);
    this.flameConfigService = getService(FlameConfigService.class);

    this.kitsConfig = this.flameConfigService.getOrCreate(this.getDataFolder(), KitsConfig.class);
    this.kitUserRepository = new KitUserRepository(
        DatastoreFactory.create(
            this.databaseConnector.getMongoClient(),
            this.networkServerCache.getCurrent().getCategory(),
            KitUser.class
        ),
        KitUser.class
    );
    this.kitUserCache = new KitUserCache(this.kitUserRepository);
    this.kitUserFactory = new KitUserFactory();

    setupCommands();
    setupListeners();

  }

  void setupListeners() {
    PluginManager pluginManager = this.getServer().getPluginManager();
    pluginManager.registerEvents(
        new UserDatabaseListener<>(this.getFlameDispatcher(), pluginManager, this.kitUserCache,
            this.kitUserRepository, this.kitUserFactory), this);
    pluginManager.registerEvents(
        new KitManagementListener(this.flameConfigService, this.kitsConfig), this);
    pluginManager.registerEvents(new KitListener(this.kitsConfig), this);
  }

  void setupCommands() {
    LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("flamehub-kits")
            .nativePermissions(false)
        )
        .argument(Location.class, new LocationArgument())
        .argument(World.class, new WorldArgument())
        .argument(Player.class, new PlayerArgument(this.messagesService))
        .argument(Kit.class, new KitArgument(this.kitsConfig))

        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(this.messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(this.messagesService))

        .commands(LiteCommandsAnnotations.of(
            new KitCommand(this.getFlameDispatcher(), this.kitsConfig, this.kitUserCache,
                this.kitUserRepository),
            new KitManagementCommand(this.flameConfigService, this.kitsConfig)
        ))

        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();
  }

}
package io.github.flamehub.warehouse;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.command.argument.LocationArgument;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.warehouse.user.WarehouseUser;
import io.github.flamehub.warehouse.user.WarehouseUserCache;
import io.github.flamehub.warehouse.user.WarehouseUserFactory;
import io.github.flamehub.warehouse.user.WarehouseUserListener;
import io.github.flamehub.warehouse.user.WarehouseUserRepository;
import io.github.flamehub.warehouse.user.WarehouseUserSaver;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

public final class WarehousePlugin extends BukkitModule {

  private WarehouseUserSaver warehouseUserSaver;
  private WarehouseUserCache warehouseUserCache;
  private WarehouseUserFactory warehouseUserFactory;
  private WarehouseUserRepository warehouseUserRepository;

  @Override
  public void onEnable() {
    super.onEnable();

    warehouseUserFactory = new WarehouseUserFactory();
    warehouseUserRepository = new WarehouseUserRepository(DatastoreFactory.create(
        databaseConnector.getMongoClient(), networkServerFacade.getCurrent().getCategory(),
        WarehouseUser.class, Warehouse.class));
    warehouseUserCache = new WarehouseUserCache(warehouseUserRepository);
    warehouseUserSaver = new WarehouseUserSaver(warehouseUserRepository, warehouseUserCache);

    final Server server = getServer();
    final BukkitScheduler scheduler = server.getScheduler();
    scheduler.runTaskTimerAsynchronously(this, warehouseUserSaver, 0L, 20 * 60L);

    final PluginManager pluginManager = server.getPluginManager();
    pluginManager.registerEvents(new WarehouseListener(warehouseUserCache), this);
    pluginManager.registerEvents(
        new WarehouseUserListener(flameDispatcher, pluginManager,
            warehouseUserCache, warehouseUserRepository, warehouseUserFactory), this);

    LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("warehouse")
            .nativePermissions(false)
        )
        .argument(Location.class, new LocationArgument())
        .argument(Player.class, new PlayerArgument(messagesService))
        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(messagesService))

        .commands(LiteCommandsAnnotations.of(
            new WarehouseCommand(warehouseUserCache),
            new WarehouseAdminCommand(networkServerFacade, networkPlayerCache, warehouseUserCache, warehouseUserRepository)
        ))

        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();

  }

  @Override
  public void onDisable() {
    super.onDisable();
    warehouseUserSaver.run();
  }

  public WarehouseUserCache getWarehouseUserCache() {
    return warehouseUserCache;
  }
}
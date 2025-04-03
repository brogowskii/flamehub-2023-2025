package io.github.flamehub.crates;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.BukkitPlugin;
import io.github.flamehub.commons.bukkit.command.argument.LocationArgument;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.argument.WorldArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.config.FlameConfigService;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

public final class CratesPlugin extends BukkitModule {

  private CratesConfig cratesConfig;
  private NetworkMessageService networkMessageService;

  @Override
  public void onEnable() {
    super.onEnable();

    cratesConfig = flameConfigService.getOrCreate(getDataFolder(), CratesConfig.class);
    networkMessageService = new NetworkMessageService(redisMessenger, "network_messages");

    final ServicesManager servicesManager = getServer().getServicesManager();
    servicesManager.register(CratesConfig.class, cratesConfig, this, ServicePriority.Normal);

    setupListeners();
    setupCommands();

  }

  void setupListeners() {
    final PluginManager pluginManager = getServer().getPluginManager();
    pluginManager.registerEvents(new CrateListener(cratesConfig, networkServerCache,
        networkMessageService, messagesService), this);
  }

  void setupCommands() {
    LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("flamehub-crates")
            .nativePermissions(false)
        )
        .argument(Location.class, new LocationArgument())
        .argument(World.class, new WorldArgument())
        .argument(Player.class, new PlayerArgument(messagesService))
        .argument(Crate.class, new CrateArgument(cratesConfig))

        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(messagesService))

        .commands(LiteCommandsAnnotations.of(
            new CrateCommand(flameConfigService, cratesConfig)
        ))

        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();
  }

}
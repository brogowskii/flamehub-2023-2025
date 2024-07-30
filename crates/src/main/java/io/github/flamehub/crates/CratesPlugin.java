package io.github.flamehub.crates;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import io.github.flamehub.commons.bukkit.BukkitPlugin;
import io.github.flamehub.commons.bukkit.command.argument.LocationArgument;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.argument.WorldArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.config.FlameConfigService;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

public final class CratesPlugin extends BukkitPlugin {

  private CratesConfig cratesConfig;
  private BukkitMessagesService messagesService;
  private FlameConfigService flameConfigService;

  @Override
  public void onEnable() {

    this.messagesService = getService(BukkitMessagesService.class);
    this.flameConfigService = getService(FlameConfigService.class);
    this.cratesConfig = this.flameConfigService.getOrCreate(this.getDataFolder(),
        CratesConfig.class);

    final ServicesManager servicesManager = this.getServer().getServicesManager();
    servicesManager.register(CratesConfig.class, this.cratesConfig, this, ServicePriority.Normal);

    setupListeners();
    setupCommands();

  }

  void setupListeners() {
    final PluginManager pluginManager = this.getServer().getPluginManager();
    pluginManager.registerEvents(new CrateListener(this, this.cratesConfig, messagesService), this);
  }

  void setupCommands() {
    LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("flamehub-crates")
            .nativePermissions(false)
        )
        .argument(Location.class, new LocationArgument())
        .argument(World.class, new WorldArgument())
        .argument(Player.class, new PlayerArgument(this.messagesService))
        .argument(Crate.class, new CrateArgument(this.cratesConfig))

        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(this.messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(this.messagesService))

        .commands(LiteCommandsAnnotations.of(
            new CrateCommand(this.flameConfigService, this.cratesConfig)
        ))

        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();
  }

}
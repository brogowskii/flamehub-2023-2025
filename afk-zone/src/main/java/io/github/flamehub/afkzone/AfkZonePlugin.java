package io.github.flamehub.afkzone;

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
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

public final class AfkZonePlugin extends BukkitModule {

  private AfkZoneConfig afkZoneConfig;

  @Override
  public void onEnable() {
    super.onEnable();

    this.afkZoneConfig = this.flameConfigService.getOrCreate(this.getDataFolder(),
        AfkZoneConfig.class);

    BukkitScheduler scheduler = this.getServer().getScheduler();
    scheduler.runTaskTimerAsynchronously(this, new AfkZoneTask(this, this.afkZoneConfig), 0L, 20L);

    PluginManager pluginManager = this.getServer().getPluginManager();
    pluginManager.registerEvents(new AfkZoneListener(this.afkZoneConfig), this);

    LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("flamehub-afk-zone")
            .nativePermissions(false)
        )
        .argument(Location.class, new LocationArgument())
        .argument(Player.class, new PlayerArgument(this.messagesService))
        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(this.messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(this.messagesService))

        .commands(LiteCommandsAnnotations.of(
            new AfkZoneCommand(flameConfigService, this.afkZoneConfig)
        ))

        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();

  }

}
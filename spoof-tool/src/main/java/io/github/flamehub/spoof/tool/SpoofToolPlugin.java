package io.github.flamehub.spoof.tool;

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
import io.github.flamehub.commons.network.player.NetworkPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class SpoofToolPlugin extends BukkitModule {

  private SpoofToolConfig spoofToolConfig;

  @Override
  public void onEnable() {
    super.onEnable();

    spoofToolConfig = flameConfigService.getOrCreate(getDataFolder(), SpoofToolConfig.class);

    final PluginManager pluginManager = getServer().getPluginManager();
    pluginManager.registerEvents(new SpoofToolListener(networkPlayerCache, networkServerCache, spoofToolConfig), this);
    LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("tiktok")
            .nativePermissions(false)
        )
        .argument(Location.class, new LocationArgument())
        .argument(Player.class, new PlayerArgument(messagesService))
        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(messagesService))

        .commands(LiteCommandsAnnotations.of(
            new SpoofToolCommand(spoofToolConfig, flameConfigService)
        ))

        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();

  }

  @Override
  public void onDisable() {
    super.onDisable();

    for (final NetworkPlayer value : networkPlayerCache.values()) {
      if (!value.getServer().equals(networkServerCache.getCurrent().getName())) {
        continue;
      }

      if (value.getProxy().equals("null")) {
        networkPlayerCache.delete(value);
      }
    }

  }
}
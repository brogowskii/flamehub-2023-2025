package io.github.flamehub.flamebox;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public final class FlameBoxPlugin extends BukkitModule {

  private FlameBoxConfig flameBoxConfig;
  
  @Override
  public void onEnable() {
    super.onEnable();
    
    flameBoxConfig = flameConfigService.getOrCreate(FlameBoxConfig.class);
    final PluginManager pluginManager = getServer().getPluginManager();
    pluginManager.registerEvents(new FlameBoxListener(flameBoxConfig), this);

    LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("flamebox")
            .nativePermissions(false)
        )
        .argument(Player.class, new PlayerArgument(messagesService))
        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))
        .missingPermission(new MissingPermissionHandlerImpl(messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(messagesService))
        .commands(LiteCommandsAnnotations.of(new FlameBoxCommand(flameConfigService, flameBoxConfig)))
        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();

  }
}
